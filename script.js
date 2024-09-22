let mediaRecorder;
let audioChunks = [];

const recordBtn = document.getElementById('recordBtn');
const stopBtn = document.getElementById('stopBtn');
const sendBtn = document.getElementById('sendBtn');
const status = document.getElementById('status');
const audioPlayback = document.getElementById('audioPlayback');
const response = document.getElementById('response');

recordBtn.addEventListener('click', async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    mediaRecorder = new MediaRecorder(stream);

    mediaRecorder.ondataavailable = (event) => {
        audioChunks.push(event.data);
    };

    mediaRecorder.onstop = () => {
        const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
        const audioUrl = URL.createObjectURL(audioBlob);
        audioPlayback.src = audioUrl;
        audioPlayback.hidden = false;
        sendBtn.disabled = false;
    };

    mediaRecorder.start();
    status.textContent = "Recording...";
    recordBtn.disabled = true;
    stopBtn.disabled = false;
});

stopBtn.addEventListener('click', () => {
    mediaRecorder.stop();
    stopBtn.disabled = true;
    recordBtn.disabled = false;
    status.textContent = "Recording stopped. You can play the audio or send it to the chatbot.";
});

sendBtn.addEventListener('click', async () => {
    const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
    const formData = new FormData();
    formData.append('file', audioBlob, 'voiceRecording.wav');

    const responseFromServer = await fetch('/upload', {
        method: 'POST',
        body: formData
    });

    const result = await responseFromServer.text();
    response.textContent = "Chatbot response: " + result;
    sendBtn.disabled = true;
    audioChunks = [];
});

