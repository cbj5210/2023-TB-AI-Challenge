import "./App.css";
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ChatBot from "./ChatBot";

function App() {
  const RedirectSite1 = () => {
    window.location.href = "/1.html";
    return <></>;
  };

  const RedirectSite2 = () => {
    window.location.href = "/2.html";
    return <></>;
  };

  const RedirectSite3 = () => {
    window.location.href = "/3.html";
    return <></>;
  };

  const RedirectSite5 = () => {
    window.location.href = "/5.html";
    return <></>;
  };

  const RedirectSite6 = () => {
    window.location.href = "/6.html";
    return <></>;
  };

  return (
    <Router>
      <Routes>
        <Route exact path="/user1" element={<RedirectSite1 />} />
        <Route exact path="/user2" element={<RedirectSite2 />} />
        <Route exact path="/user3" element={<RedirectSite3 />} />
        <Route exact path="/user5" element={<RedirectSite5 />} />
        <Route exact path="/user6" element={<RedirectSite6 />} />
        <Route path="/" exact element={<ChatBot />} />
        <Route path="/:idParam" element={<ChatBot />} />
      </Routes>
    </Router>
  );
}

export default App;
