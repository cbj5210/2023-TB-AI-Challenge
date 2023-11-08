import "./App.css";
import { firestore } from "./Firebase";
import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import InfiniteScroll from "react-infinite-scroll-component";
import { Button, Input, Space, Divider, List, Skeleton, Avatar } from "antd";

function ChatBot() {
  const [data, setData] = useState([]);
  const [input, setInput] = useState("");
  const bottomEl = useRef(null);
  const {idParam} = useParams();
  const [userProfile, setUserProfile] = useState(
      [{id: "1111111", name: "정주상", image: "../images/1111111.png"},
        {id: "2222222", name: "최병준", image: "../images/2222222.png"},
        {id: "3333333", name: "주민범", image: "../images/3333333.png"},
        {id: "5555555", name: "최하혁", image: "../images/5555555.png"},
        {id: "6666666", name: "김영래", image: "../images/6666666.png"}]);

  const loadMoreData = () => {
  };

  const scrollToBottom = () => {
    bottomEl?.current?.scrollIntoView({ behavior: "auto" });
  };

  useEffect(() => {

    const userId = idParam ? idParam : "1111111";

    firestore
    .collection("TBAI")
    .where("user", "==", userId)
    .orderBy("createTime", "asc")
    .onSnapshot(snapshot => {
      const firebaseData = snapshot.docs.map(doc => ({

        user: doc.data().user,
        type: doc.data().type,
        message: doc.data().message,
        responseType: doc.data().responseType,
        id: doc.id,
        title: doc.data().type === "request" ? getUserName(userId) : "시크릿 T",
        avatarSrc:
            doc.data().type === "request"
                ? getUserImage(userId)
                : "../images/logo.png",
      }));

      setData(firebaseData);
    })
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [data]);

  const handleEnterSubmit = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      return handleSubmit();
    }
  }

  const handleSubmit = async () => {

    const userId = idParam ? idParam : "1111111";

    if (input !== "") {
      const date = new Date();
      const dateFormat =
          date.getFullYear() +
          "-" +
          ("0" + (date.getMonth() + 1)).slice(-2) +
          "-" +
          ("0" + date.getDate()).slice(-2) +
          " " +
          ("0" + date.getHours()).slice(-2) +
          ":" +
          ("0" + date.getMinutes()).slice(-2) +
          ":" +
          ("0" + date.getSeconds()).slice(-2) +
          ":" +
          ("00" + date.getMilliseconds()).slice(-3);

      await firestore.collection("TBAI").add({
        user: userId,
        type: "request",
        message: input,
        createTime: dateFormat,
        solved: "false"
      });

      setInput("");
    }
  };

  function getUserName(id) {
    const user = userProfile.find(user => user.id === id);

    return user ? user.name : "유저를 찾을 수 없습니다.";
  }

  function getUserImage(id) {
    const user = userProfile.find(user => user.id === id);

    return user ? user.image : "";
  }

  return (
      <div
          id="scrollableDiv"
          style={{
            height: 400,
            overflow: "auto",
            padding: "0 16px",
            border: "1px solid rgba(140, 140, 140, 0.35)",
          }}
      >
        <InfiniteScroll
            dataLength={data.length}
            next={loadMoreData}
            loader={
              <Skeleton
                  avatar
                  paragraph={{
                    rows: 1,
                  }}
                  active
              />
            }
            endMessage={<Divider plain></Divider>}
            scrollableTarget="scrollableDiv"
        >
          <List
              dataSource={data}
              renderItem={(item) => (
                  <List.Item key={item.id}>
                    <List.Item.Meta
                        avatar={<Avatar src={item.avatarSrc} />}
                        title={item.title}
                        description={item.message}
                    />
                  </List.Item>
              )}
          />
          <div ref={bottomEl}></div>
        </InfiniteScroll>

        <Space.Compact style={{ width: "100%" }}>
          <Input
              defaultValue=""
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={handleEnterSubmit}
              placeholder="요청을 입력해주세요."
          />
          <Button type="primary" onClick={handleSubmit}>
            Submit
          </Button>
        </Space.Compact>
      </div>
  );
}

export default ChatBot;
