#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
SUFFIX="$(date +%s)"
EMAIL="bash_test_${SUFFIX}@example.com"
USERNAME="bash_test_${SUFFIX}"
PASSWORD="StrongPass123"
UPDATED_USERNAME="${USERNAME}_updated"

HTTP_CODE=""
RESPONSE_BODY=""

pretty_json() {
  local raw="$1"
  if python3 -m json.tool >/dev/null 2>&1 <<<"$raw"; then
    python3 -m json.tool <<<"$raw"
  else
    echo "$raw"
  fi
}

json_get() {
  local raw="$1"
  local path="$2"
  python3 -c 'import json, sys
raw = sys.argv[1]
path = sys.argv[2]
current = json.loads(raw)
for part in path.split("."):
    if isinstance(current, dict) and part in current:
        current = current[part]
    elif isinstance(current, list) and part.isdigit() and int(part) < len(current):
        current = current[int(part)]
    else:
        sys.exit(1)
if isinstance(current, (dict, list)):
    print(json.dumps(current))
else:
    print(current)' "$raw" "$path"
}

print_response() {
  local title="$1"
  echo
  echo "========== ${title} =========="
  echo "HTTP ${HTTP_CODE}"
  pretty_json "$RESPONSE_BODY"
}

request_json() {
  local method="$1"
  local path="$2"
  local payload="${3:-}"
  local token="${4:-}"

  local tmp_response_file
  tmp_response_file="$(mktemp)"

  local -a cmd=(curl -sS -o "$tmp_response_file" -w "%{http_code}" -X "$method" "${BASE_URL}${path}" -H "Content-Type: application/json")
  if [[ -n "$token" ]]; then
    cmd+=( -H "Authorization: Bearer ${token}" )
  fi
  if [[ -n "$payload" ]]; then
    cmd+=( -d "$payload" )
  fi

  HTTP_CODE="$("${cmd[@]}")"
  RESPONSE_BODY="$(cat "$tmp_response_file")"
  rm -f "$tmp_response_file"
}

assert_http() {
  local expected="$1"
  local context="$2"
  if [[ "$HTTP_CODE" != "$expected" ]]; then
    echo "${context} expected HTTP ${expected}, got ${HTTP_CODE}" >&2
    exit 1
  fi
}

assert_json_value() {
  local path="$1"
  local expected="$2"
  local context="$3"
  local actual
  actual="$(json_get "$RESPONSE_BODY" "$path")"
  if [[ "$actual" != "$expected" ]]; then
    echo "${context} expected ${path}=${expected}, got ${actual}" >&2
    exit 1
  fi
}

echo "API smoke test against ${BASE_URL}"
echo "Test user: ${EMAIL}"

REGISTER_PAYLOAD=$(cat <<JSON
{
  "email": "${EMAIL}",
  "username": "${USERNAME}",
  "password": "${PASSWORD}"
}
JSON
)

request_json "POST" "/auth/register" "$REGISTER_PAYLOAD"
print_response "POST /auth/register"
assert_http "201" "Register"
assert_json_value "success" "True" "Register"

AUTH_TOKEN="$(json_get "$RESPONSE_BODY" "data.token")"
if [[ -z "$AUTH_TOKEN" ]]; then
  echo "Register did not return a token" >&2
  exit 1
fi

LOGIN_PAYLOAD=$(cat <<JSON
{
  "email": "${EMAIL}",
  "password": "${PASSWORD}"
}
JSON
)

request_json "POST" "/auth/login" "$LOGIN_PAYLOAD"
print_response "POST /auth/login"
assert_http "200" "Login"
assert_json_value "success" "True" "Login"

request_json "GET" "/users/me" "" "$AUTH_TOKEN"
print_response "GET /users/me"
assert_http "200" "Get current user"
assert_json_value "data.email" "$EMAIL" "Get current user"

USER_ID="$(json_get "$RESPONSE_BODY" "data.id")"

UPDATE_PAYLOAD=$(cat <<JSON
{
  "username": "${UPDATED_USERNAME}"
}
JSON
)

request_json "PUT" "/users/me" "$UPDATE_PAYLOAD" "$AUTH_TOKEN"
print_response "PUT /users/me"
assert_http "200" "Update current user"
assert_json_value "data.username" "$UPDATED_USERNAME" "Update current user"

request_json "GET" "/users/${USER_ID}" "" "$AUTH_TOKEN"
print_response "GET /users/{id}"
assert_http "200" "Get user by id"
assert_json_value "data.id" "$USER_ID" "Get user by id"

request_json "DELETE" "/users/me" "" "$AUTH_TOKEN"
print_response "DELETE /users/me"
assert_http "200" "Delete current user"
assert_json_value "success" "True" "Delete current user"

request_json "POST" "/auth/login" "$LOGIN_PAYLOAD"
print_response "POST /auth/login (after delete)"
assert_http "401" "Login after delete"

echo
echo "All endpoint tests passed."

