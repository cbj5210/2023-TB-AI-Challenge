import "./App.css";
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ChatBot from "./ChatBot";
import Tnet from "./Tnet";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" exact element={<ChatBot />} />
        <Route path="/:idParam" element={<ChatBot />} />
        <Route path="/tnet/:idParam" element={<Tnet />} />
      </Routes>
    </Router>
  );
}

export default App;
