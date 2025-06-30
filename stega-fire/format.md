# Fire Effect - Copilot exercise - Vibe coding

Category: Stega
Level: Medium ?



## Description of the challenge

It all start with [SourceMap header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/SourceMap)

> The HTTP SourceMap header has precedence over a source annotation (sourceMappingURL=path-to-map.js.map), and if both are present, the header URL is used to resolve the source map file.

So what if I have both with different URL to hide some message ?

Step 1: Create a random app with enough JS to have a good minified js map.
Step 2: Include the hidden message with some small change (can be customized)

## For the challenger

Nice app, but can you found the secret mesaage in all those vibes ?
-> URL

## Build

You need to customize the `Dockerfile` with the flag (on the line `RUN node hideFlag.js`)
TODO: use buildargs ?

```
docker build -t ctf/`basename "$PWD"` .
```

## Run (local)

```
docker run -p 80:80  ctf/`basename "$PWD"`
```


## Solve (quickwin, we know the trick)

```
wget http://localhost/fire.min.js.map # The non-modified one, served with sourceMap header
wget http://localhost/js/fire.min.js.map # The slightly modified. Url present in comment at the end of /js/fire.min.js
cmp -l -b fire.min.js.map fire.min.js.map.1
```

## Technical details

- hideFlag.js contains the small script to replace letter
- nginx.conf: set the header SourceMap for one url
- DockerFile: handle the build and calling/setup all the file
- the app: nothing usefull for the chall

## Level of difficulties

- Can be easier if we add the sourceMap header on several file (like index.html)
    - SourceMap on header can show a "you are in a good way"

- We can add/remove some deadend in the main script
    - palette contains a list of color, but nothing hide here. Either we clarify, missleads, or do nothing
    - Same for the random note

## Todo

- Nginx is not hardened. (neither the Dockerfile), but this is not a hack right ?
- Main app can so some thing?
- Take build arg for the secret when building the docker.



