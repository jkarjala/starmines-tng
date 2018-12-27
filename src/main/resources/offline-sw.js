//This is the "Offline copy of pages" service worker

//Install stage sets up the index page (home page) in the cache and opens a new cache
self.addEventListener('install', function(event) {
  var indexPage = new Request('index.html');
  event.waitUntil(
    fetch(indexPage).then(function(response) {
      return caches.open('starmines-offline').then(function(cache) {
        console.log('[SMTNG] Cached index page during Install '+ response.url);
        return cache.put(indexPage, response);
      });
  }));
});

//If any fetch fails, it will look for the request in the cache and serve it from there first
self.addEventListener('fetch', function(event) {
  var updateCache = function(request){
    return caches.open('starmines-offline').then(function (cache) {
      return fetch(request).then(function (response) {
        console.log('[SMTNG] add page to offline '+response.url)
        return cache.put(request, response);
      });
    });
  };

  if (event.request.method=='GET') {
    event.waitUntil(updateCache(event.request));
  }
  else {
    // Could store the POST content to local storage and POST later when connected...
    console.log('[SMTNG] no offline for ' + event.request.method + " " + event.request.url)
  }

  event.respondWith(
    fetch(event.request).catch(function(error) {
      console.log( '[SMTNG] Network request Failed. Serving content from cache: ' + error );

      //Check to see if you have it in the cache
      //Return response
      //If not in the cache, then return error page
      return caches.open('starmines-offline').then(function (cache) {
        return cache.match(event.request).then(function (matching) {
          var report =  !matching || matching.status == 404 ? Promise.reject('no-match'): matching;
          return report
        });
      });
    })
  );
})
