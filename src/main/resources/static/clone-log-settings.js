define('plugin/log-on-clone/repository-settings', [
  'jquery',
  'aui',
  'bitbucket/util/server',
  'bitbucket/util/navbuilder',
  'bitbucket/util/state',
  'exports'
], function($, AJS, server, nav, pageState, exports) {
  function resourceUrl(resourceName) {
    return nav
      .rest('log-on-clone')
      .currentRepo()
      .addPathComponents(resourceName)
      .build();
  }

  var formSelector = '#log-on-clone-settings-form';
  var inputSelector = 'input#url';
  var spinnerTemplate = '<div class="button-spinner"></div>';

  function init() {
    var $form = $(formSelector);
    var $input = $(inputSelector);
    var $submit = $form.find('#log-on-clone-settings-form-submit');
    var $spinner;

    function addSpinner() {
      $spinner = $(spinnerTemplate).insertAfter($submit);
      $spinner.spin();
    }

    function removeSpinner() {
      $spinner && $spinner.remove();
      $spinner = null;
    }

    function setInputEnabled(enabled) {
      if (enabled) {
        $input.removeAttr('disabled').removeClass('disabled');
      } else {
        $input.attr('disabled', 'disabled').addClass('disabled');
      }
    }

    function setSubmitEnabled(enabled) {
      if (enabled) {
        $submit.removeAttr('disabled').removeClass('disabled');
      } else {
        $submit.attr('disabled', 'disabled').addClass('disabled');
      }
    }

    function setValue(value) {
      $input.val(value);
    }

    $form.submit(function(event) {
      event.preventDefault();

      addSpinner();
      setInputEnabled(false);
      setSubmitEnabled(false);

      server
        .rest({
          url: resourceUrl('settings'),
          type: 'POST',
          data: {
            url: $input.val()
          }
        })
        .always(function() {
          removeSpinner();
          setInputEnabled(true);
          setSubmitEnabled(true);
        });
    });

    setInputEnabled(false);
    setSubmitEnabled(false);

    server
      .rest({
        url: resourceUrl('settings'),
        type: 'GET'
      })
      .then(function(response) {
        if (response) {
          setValue(response.url);
        }

        setInputEnabled(true);
        setSubmitEnabled(true);
      });
  }

  exports.onReady = function() {
    $(document).ready(function() {
      init();
    });
  };
});
