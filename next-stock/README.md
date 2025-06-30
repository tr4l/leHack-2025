# Next Stock

## Build
Build with custom (need to be hardcoded) password

docker build --build-arg ACTION_PASSWORD="the-pass" -t ssa .

Run with the flag

docker run -e THE_FLAG="the nice flag" -p 3000:3000 ssa


Next.JS app, with server action

1. getTitleAction: Used on first page, so we have an example
2. getSymbolForAction: Not used, but if we pass a "function" as args, we have the "source" of the server function
3. getFlagAction: Not used, if we pass the password (that we found on the source), we have the flag

## Deploy

Only give URL of the server, no explanation.

## Category

Hack


## Difficuty

Hard (I think), because this is really not easy to understand how Next.js work internally

We can change the JS to make the action more clear, or give them a meaning

## Solve

Sample request are on request.http, but in short:
- Call the server action getSymbolFor with the function getFlag (the most complicated call)
- Call getFlag action with the password we got

## Todo

- harden
- update 
