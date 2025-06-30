import * as Tone from 'tone';

document.addEventListener('DOMContentLoaded', () => {
    const app = document.getElementById('app');
    if (app) {
        app.innerHTML = '<h1>Hello, TypeScript!</h1>';
    }
});

const canvas = document.getElementById('mainCanvas') as HTMLCanvasElement;
const ctx = canvas.getContext('2d');

canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

const fireWidth = Math.floor(canvas.width / 4);
const fireHeight = Math.floor(canvas.height / 4);
const firePixels = new Array(fireWidth * fireHeight).fill(0);

let konamiCode = ["ArrowUp", "ArrowUp", "ArrowDown", "ArrowDown", "ArrowLeft", "ArrowRight", "ArrowLeft", "ArrowRight", "b", "a"];
let konamiIndex = 0;
let isChillMode = false;

function handleKonamiCode(event: KeyboardEvent) {
    if (event.key === konamiCode[konamiIndex]) {
        konamiIndex++;
        if (konamiIndex === konamiCode.length) {
            toggleChillMode();
            konamiIndex = 0;
        }
    } else {
        konamiIndex = 0;
    }
}

function toggleChillMode() {
    isChillMode = !isChillMode;
}

function createFireSource() {
    for (let x = 0; x < fireWidth; x++) {
        firePixels[(fireHeight - 1) * fireWidth + x] = 36;
    }
}

function calculateFirePropagation() {
    for (let y = 1; y < fireHeight; y++) {
        for (let x = 0; x < fireWidth; x++) {
            const src = y * fireWidth + x;
            const decay = Math.floor(Math.random() * 3);
            const dst = src - decay - fireWidth;
            if (dst >= 0 && dst < firePixels.length) {
                firePixels[dst] = Math.max(firePixels[src] - decay, 0);
            }
        }
    }
}

function renderFire() {
    const imageData = ctx.createImageData(fireWidth, fireHeight);
    for (let i = 0; i < firePixels.length; i++) {
        const value = firePixels[i];
        const color = valueToColor(value);
        const index = i * 4;
        imageData.data[index] = color[0];
        imageData.data[index + 1] = color[1];
        imageData.data[index + 2] = color[2];
        imageData.data[index + 3] = 255;
    }
    ctx.putImageData(imageData, 0, 0);

    // Scale up the fire effect to fit the canvas
    ctx.drawImage(canvas, 0, 0, fireWidth, fireHeight, 0, 0, canvas.width, canvas.height);
}

function valueToColor(value: number): [number, number, number] {
    if (isChillMode) {
        const chillPalette = [
            [7, 31, 31], [15, 47, 47], [23, 71, 71], [31, 87, 87],
            [47, 87, 87], [71, 103, 103], [87, 119, 119], [103, 143, 143],
            [119, 159, 159], [143, 175, 175], [159, 191, 191], [175, 199, 199],
            [191, 223, 223], [199, 223, 223], [223, 223, 223], [223, 215, 215],
            [223, 207, 207], [223, 199, 199], [223, 191, 191], [223, 183, 183],
            [223, 175, 175], [223, 167, 167], [223, 159, 159], [223, 151, 151],
            [223, 143, 143], [223, 135, 135], [223, 127, 127], [223, 119, 119],
            [223, 111, 111], [223, 103, 103], [223, 95, 95], [223, 87, 87],
            [223, 79, 79], [223, 71, 71], [223, 63, 63], [223, 55, 55],
            [223, 47, 47], [223, 39, 39], [223, 31, 31], [223, 23, 23],
            [223, 15, 15], [223, 7, 7], [223, 0, 0]
        ];
        return chillPalette[value] || [0, 0, 0];
    }

    const palette = [
        [7, 7, 31], [15, 15, 47], [23, 23, 71], [31, 31, 87],
        [47, 15, 87], [71, 15, 103], [87, 23, 119], [103, 31, 143],
        [119, 31, 159], [143, 39, 175], [159, 47, 191], [175, 63, 199],
        [191, 71, 223], [199, 79, 223], [223, 87, 223], [223, 95, 215],
        [223, 103, 215], [223, 111, 207], [223, 119, 207], [223, 127, 207],
        [223, 135, 199], [223, 143, 199], [223, 151, 191], [223, 159, 191],
        [223, 167, 183], [223, 175, 183], [223, 183, 175], [223, 191, 175],
        [223, 199, 167], [223, 207, 159], [223, 215, 151], [223, 223, 143],
        [223, 223, 135], [223, 223, 127], [223, 223, 119], [223, 223, 111],
        [223, 223, 103], [223, 223, 95], [223, 223, 87], [223, 223, 79],
        [223, 223, 71], [223, 223, 63], [223, 223, 55], [223, 223, 47],
        [223, 223, 39], [223, 223, 31], [223, 223, 23], [223, 223, 15],
        [223, 223, 7], [223, 223, 0]
    ];
    return palette[value] || [0, 0, 0];
}

function loop() {
    calculateFirePropagation();
    renderFire();
    requestAnimationFrame(loop);
}

function playRandomRetroSound() {
    const synth = new Tone.Synth({
        oscillator: {
            type: 'square'
        }
    }).toDestination();

    const now = Tone.now();
    const note = ['C4', 'E4', 'G4', 'B4', 'D4', 'F4', 'A4', 'C5', 'E5', 'G5', 'B5', 'D5', 'F5', 'A5', 'C6', 'E6'][Math.floor(Math.random() * 16)];
    synth.triggerAttackRelease(note, '8n', now);

    // Schedule the next sound at a random interval
    setTimeout(playRandomRetroSound, Math.random() * 3000 + 2000); // Between 3-5 seconds
}

document.getElementById('startButton')?.addEventListener('click', () => {
    const splashScreen = document.getElementById('splashScreen');
    const canvas = document.getElementById('mainCanvas');

    if (splashScreen && canvas) {
        splashScreen.style.display = 'none';
        canvas.style.display = 'block';
    }

    // Resume the AudioContext after user gesture
    Tone.start().then(() => {
        console.log('AudioContext started');
        playRandomRetroSound();
        createFireSource();
        loop();
    }).catch((err) => {
        console.error('Error starting AudioContext:', err);
    });
});

window.addEventListener("keydown", handleKonamiCode);
