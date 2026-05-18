#!/bin/sh
set -eu

# Write runtime config into a JS file served by Nginx
cat <<EOF > /usr/share/nginx/html/config.js
window.__APP_CONFIG__ = {
  API_BASE_URL: "${API_BASE_URL:-/api}"
};
EOF

exec nginx -g "daemon off;"