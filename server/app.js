console.log("Hello World!");

var robot = require("robotjs");

const dgram = require('dgram');
const server = dgram.createSocket('udp4');

var setGas = (isOn) => {
    robot.keyToggle("up", isOn ? "down" : "up");
};

var setBrake = (isOn) => {
    robot.keyToggle("down", isOn ? "down" : "up");
};

var setHandBrake = (isOn) => {
    robot.keyToggle("space", isOn ? "down" : "up");
};

var wheelLeftTimeout;

var wheelLeft = () => {
    robot.keyToggle("left", "down");
    clearTimeout(wheelLeftTimeout);
    wheelLeftTimeout = setTimeout(() => {
        robot.keyToggle("left", "up");
    }, 10);

};

var wheelRightTimeout;

var wheelRight = () => {
    robot.keyToggle("right", "down");
    clearInterval(wheelRightTimeout);
    wheelRightTimeout = setTimeout(() => {
        robot.keyToggle("right", "up");
    }, 10);
};

server.on('error', (err) => {
    console.log(`server error:\n${err.stack}`);
    server.close();
});

var deadZone = 5;
var maxDegree = 30;
var wheelRatio = 200;

var disablePedalsTimeout;
var disableWheelTimeout;
var currentDegree = 0.0;
var elapsedTime = 0;
var lastClickTime = 0;

var wheelInterval = setInterval(() => {
    elapsedTime++;
    if (Math.abs(currentDegree) > deadZone) {
        var isLeft = currentDegree > 0;
        var degreeValue = (1 - (Math.min(Math.abs(currentDegree), maxDegree) / maxDegree)) * wheelRatio;
        if (elapsedTime - lastClickTime > degreeValue) {
            isLeft ? wheelLeft() : wheelRight();
            console.log(degreeValue);
            lastClickTime = elapsedTime;
        }
    }
}, 1);

server.on('message', (msg, rinfo) => {
    //console.log(`server got: ${msg} from ${rinfo.address}:${rinfo.port}`);
    var dataArray = msg.toString().split(' ');
    currentDegree = parseFloat(dataArray[0]);
    var isGasOn = dataArray[1] === 'true';
    var isBrakeOn = dataArray[2] === 'true';
    var isHandBrakeOn = dataArray[3] === 'true';

    clearTimeout(disablePedalsTimeout);
    disablePedalsTimeout = setTimeout(() => {
        setBrake(false);
        setGas(false);
        setHandBrake(false);
    }, 1000);
    clearTimeout(disableWheelTimeout);
    disableWheelTimeout = setTimeout(() => {
        currentDegree = 0.0;
    }, 1000);

    setGas(isGasOn);
    setBrake(isBrakeOn);
    setHandBrake(isHandBrakeOn);
});

server.on('listening', () => {
    var address = server.address();
console.log(`server listening ${address.address}:${address.port}`);
});

server.bind(3000);