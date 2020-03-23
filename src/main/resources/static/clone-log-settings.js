define('plugin/log-on-clone/repository-settings', [
  'jquery',
  'bitbucket/util/server',
  'bitbucket/util/navbuilder',
  'exports'
], function($, server, nav, exports) {
  function resourceUrl(resourceName) {
    return nav
      .rest('log-on-clone')
      .currentRepo()
      .addPathComponents(resourceName)
      .build();
  }

  var formSelector = '#log-on-clone-settings-form';
  var urlInputSelector = 'input#url';
  var enabledInputSelector = 'input#enabled';
  var successTemplate = '<span class="button-success aui-icon aui-icon-small aui-iconfont-check">Done!</span>';
  var spinnerTemplate = '<div class="button-spinner"></div>';

  function init() {
    var $form = $(formSelector);
    var $urlInput = $(urlInputSelector);
    var $enabledInput = $(enabledInputSelector);
    var $submit = $form.find('#log-on-clone-settings-form-submit');
    var $spinner;
    var $success;

    function addSpinner() {
      $spinner = $(spinnerTemplate).insertAfter($submit);
      $spinner.spin();
    }

    function removeSpinner() {
      $spinner && $spinner.remove();
      $spinner = null;
    }

    function flashSuccess() {
      $success = $(successTemplate).insertAfter($submit);
      setTimeout(function() {
        $success && $success.remove();
        $success = null;
      }, 1000);
    }

    function setEnabled($el, enabled) {
      if (enabled) {
        $el.removeAttr('disabled').removeClass('disabled');
      } else {
        $el.attr('disabled', 'disabled').addClass('disabled');
      }
    }

    function setUrlValue(value) {
      $urlInput.val(value);
    }

    function setEnabledValue(value) {
      $enabledInput.prop('checked', value);
    }

    $form.submit(function(event) {
      event.preventDefault();

      addSpinner();
      setEnabled($urlInput, false);
      setEnabled($enabledInput, false);
      setEnabled($submit, false);

      server
        .rest({
          url: resourceUrl('settings'),
          type: 'POST',
          data: {
            url: $urlInput.val(),
            enabled: $enabledInput.prop('checked')
          }
        })
        .always(function() {
          removeSpinner();
          flashSuccess();
          setEnabled($urlInput, true);
          setEnabled($enabledInput, true);
          setEnabled($submit, true);
        });
    });

    addSpinner();
    setEnabled($urlInput, false);
    setEnabled($enabledInput, false);
    setEnabled($submit, false);

    server
      .rest({
        url: resourceUrl('settings'),
        type: 'GET'
      })
      .then(function(response) {
        if (response) {
          setUrlValue(response.url);
          setEnabledValue(response.enabled);
        }

        removeSpinner();
        setEnabled($urlInput, true);
        setEnabled($enabledInput, true);
        setEnabled($submit, true);
      });
  }

  exports.onReady = function() {
    $(document).ready(function() {
      init();
    });
  };
});
