// Keeps Hub opening without a connection. Network first (no-cache skips the browser's own copy), so an update shows at once.
const VERSION = 'hub-1.6.0';
const FILES = ['./', 'index.html', 'manifest.webmanifest', 'icon.svg', 'icons/icon-180.png', 'icons/icon-512.png',
  ...['Regular', 'SemiBold', 'Italic'].map(w => `fonts/NotoSerif-${w}.ttf`),
  ...['hora', 'consensus', 'augur', 'annales', 'kalendae', 'caelum', 'fidelis'].map(a => `icons/${a}.png`)];

self.addEventListener('install', e => {
  // one missing icon mustn't stop Hub working offline, so each file is cached on its own
  e.waitUntil(caches.open(VERSION).then(c => Promise.all(FILES.map(f => c.add(f).catch(() => {})))).then(() => self.skipWaiting()));
});
self.addEventListener('activate', e => {
  e.waitUntil(caches.keys().then(ks => Promise.all(ks.filter(k => k !== VERSION).map(k => caches.delete(k)))).then(() => self.clients.claim()));
});
self.addEventListener('fetch', e => {
  const url = new URL(e.request.url);
  if (e.request.method !== 'GET' || url.origin !== location.origin) return;
  e.respondWith(fetch(e.request, { cache: 'no-cache' }).then(r => {
    if (r.ok) { const copy = r.clone(); caches.open(VERSION).then(c => c.put(e.request, copy)); }
    return r;
  }).catch(() => caches.match(e.request, { ignoreSearch: true })));
});
