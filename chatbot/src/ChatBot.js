import "./App.css";
import { firestore } from "./Firebase";
import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import InfiniteScroll from "react-infinite-scroll-component";
import { Button, Input, Space, Divider, List, Skeleton, Avatar } from "antd";

function ChatBot() {
  const [data, setData] = useState([]);
  const [input, setInput] = useState("");
  const { idParam } = useParams();

  /* 사용자 프로필 (사번, 이름, 프로필 이미지) */
  const [userProfile, setUserProfile] = useState([
    { id: "1111111", name: "정주상", image: "../images/1111111.png" },
    { id: "2222222", name: "최병준", image: "../images/2222222.png" },
    { id: "3333333", name: "주민범", image: "../images/3333333.png" },
    { id: "5555555", name: "최하혁", image: "../images/5555555.png" },
    { id: "6666666", name: "김영래", image: "../images/6666666.png" },
  ]);

  const bottomEl = useRef(null);
  const loadMoreData = () => {};

  const scrollToBottom = () => {
    bottomEl?.current?.scrollIntoView({ behavior: "auto" });
  };

  // 시크릿 T 와 대화이력이 없는 경우 예시 검색어 제공
  const endMessageSet = [
    "시크릿 T 에게 궁금한 내용을 입력해주세요.",
    "- 주민범님, 최하혁님, 김영래님 근무 시간 알려줘",
    "- 캔미팅 근무 등록 어떻게 해?",
    "- 오늘 구내식당 메뉴 뭐야?",
  ];

  useEffect(() => {
    const userId = idParam ? idParam : "1111111";

    firestore
      .collection("TBAI")
      .where("user", "==", userId)
      .orderBy("createTime", "asc")
      .onSnapshot((snapshot) => {
        const firebaseData = snapshot.docs.map((doc) => ({
          user: doc.data().user,
          type: doc.data().type,
          message: doc.data().message,
          responseType: doc.data().responseType,
          id: doc.id,
          title:
            doc.data().type === "request" ? getUserName(userId) : "시크릿 T",
          avatarSrc:
            doc.data().type === "request"
              ? getUserImage(userId)
              : "../images/logo.png",
        }));

        setData(firebaseData);
      });
  }, []);

  // 시크릿 T 와 대화 이력이 존재하는 경우, 하단으로 스크롤 이동
  useEffect(() => {
    if (data.length > 2) scrollToBottom();
  }, [data]);

  const handleEnterSubmit = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      return handleSubmit();
    }
  };

  // 제출 시 사용자 아이디, 날짜, 메시지를 지정하여 firestore 에 저장
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
        solved: "false",
      });

      setInput("");
    }
  };

  function getUserName(id) {
    const user = userProfile.find((user) => user.id === id);
    return user ? user.name : "유저를 찾을 수 없습니다.";
  }

  function getUserImage(id) {
    const user = userProfile.find((user) => user.id === id);
    return user ? user.image : "";
  }

  function getFileMessage(item) {
    return (
      <p>
        파일을 발급해드릴게요 ^^{" "}
        <a href={item.message} target="_blank">
          다운로드 받기
        </a>{" "}
      </p>
    );
  }

  return (
    <div
      id="scrollableDiv"
      style={{
        height: 335,
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
        endMessage={
          <div style={{ textAlign: "center" }}>
            {" "}
            {data.length !== 0
              ? ""
              : endMessageSet.map((line) => {
                  return (
                    <span>
                      {line}
                      <br />
                    </span>
                  );
                })}
            <Divider plain></Divider>
          </div>
        }
        scrollableTarget="scrollableDiv"
      >
        <List
          dataSource={data}
          renderItem={(item) => (
            <List.Item key={item.id}>
              <List.Item.Meta
                avatar={<Avatar src={item.avatarSrc} />}
                title={item.title}
                description={
                  item.responseType === "file"
                    ? getFileMessage(item)
                    : item.message
                }
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
