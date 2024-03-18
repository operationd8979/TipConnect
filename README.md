# About The Project!

A messaging and calling website, experimented with live streaming using the P2P model of WebRTC.
```
This is the source code for the server, which is a part of the project. You can refer to the user interface source code built with ReactJS at the following link:
https://github.com/operationd8979/TipConnect-ClientSide
```

## Build with
  
The server is written in Java using the Spring Boot framework. This project serves as my coursework to familiarize myself with programming principles and design patterns to effectively manage source code.


## Features:
- RESTful API.
- User authentication combined with JWT filter before Spring Security filter.
- Handling messages via WebSocket, managing user sessions to accurately forward messages and notify when users are online.
- Handling SDP exchange for establishing peer-to-peer connections and managing reconnections for lower node users to resume live stream viewing when an upper node user disconnects.

<p float="left">
  <img src="/resource/images/shots/001.png" width="800">
</p>

The live stream solution allows each user to establish maximum of 3 P2P connections, and the server is responsible for managing the user nodes into a tree network. When an middle node disconnects, the server sends signal information of a replacement node to the lower node requesting reconnection.


<p float="left">
  <img src="/resource/images/shots/002.png" width="800">
</p>
<p float="left">
  <img src="/resource/images/shots/003.png" width="800">
</p>
<p float="left">
  <img src="/resource/images/shots/004.png" width="800">
</p>
