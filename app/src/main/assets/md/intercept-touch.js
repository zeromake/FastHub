document.addEventListener("DOMContentLoaded", function(event) {
   document.querySelectorAll('img').forEach(function(img){
  	img.onerror = function(){this.style.display='none';};
   });
});

window.onload = function() {
    addTouchEvents(document.getElementsByTagName("pre"));
    addTouchEvents(document.getElementsByTagName("table"));
    var hash = window.location.hash.substr(1);
    if (hash != ""){
        scrollTo(hash);
    }
};

function debounce(func, wait, immediate){
  var timeout, args, context, timestamp, result;
  if (null == wait) wait = 100;
  function later() {
    var last = Date.now() - timestamp;
    if (last < wait && last >= 0) {
      timeout = setTimeout(later, wait - last);
    } else {
      timeout = null;
      if (!immediate) {
        result = func.apply(context, args);
        context = args = null;
      }
    }
  };

  var debounced = function(){
    context = this;
    args = arguments;
    timestamp = Date.now();
    var callNow = immediate && !timeout;
    if (!timeout) timeout = setTimeout(later, wait);
    if (callNow) {
      result = func.apply(context, args);
      context = args = null;
    }
    return result;
  };
  debounced.clear = function() {
    if (timeout) {
      clearTimeout(timeout);
      timeout = null;
    }
  };

  debounced.flush = function() {
    if (timeout) {
      result = func.apply(context, args);
      context = args = null;
      clearTimeout(timeout);
      timeout = null;
    }
  };
  return debounced;
};

function addTouchEvents(elements) {
    for (var i = 0; i < elements.length; i++) {
        elements[i].addEventListener("touchstart", touchStart, false);
        elements[i].addEventListener("touchend", touchEnd, false);
    }
}

function touchStart(event) {
    Android.startIntercept();
}

function touchEnd(event) {
    Android.stopIntercept();
}