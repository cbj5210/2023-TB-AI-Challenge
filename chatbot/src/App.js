import "./App.css";
import { firestore } from "./Firebase";
import React, { useEffect, useState } from "react";
import InfiniteScroll from "react-infinite-scroll-component";
import { Button, Input, Space, Divider, List, Skeleton, Avatar } from "antd";

function App() {
  const [data, setData] = useState([]);
  const [input, setInput] = useState("");

  const loadMoreData = () => {
  };

  useEffect(() => {
    firestore
    .collection("TBAI")
    .orderBy("createTime", "asc")
    .onSnapshot(snapshot => {
      const firebaseData = snapshot.docs.map(doc => ({

        user: doc.data().user,
        type: doc.data().type,
        message: doc.data().message,
        responseType: doc.data().responseType,
        id: doc.id,
        title: doc.data().type === "request" ? "정주상" : "시크릿 T",
        avatarSrc:
            doc.data().type === "request"
                ? "../images/profile.png"
                : "../images/logo.png",
      }));

      setData(firebaseData);
    })
  }, []);

  const handleEnterSubmit = (e) => {
    if (e.key === 'Enter') {
      return handleSubmit();
    }
  }

  const handleSubmit = async () => {

    if (input !== '') {
      const date = new Date();
      const dateFormat = date.getFullYear() + "-"
          + ("0" + (date.getMonth() + 1)).slice(-2) + "-"
          + ("0" + date.getDate()).slice(-2)
          + " "
          + ("0" + date.getHours() ).slice(-2) + ":"
          + ("0" + date.getMinutes()).slice(-2) + ":"
          + ("0" + date.getSeconds()).slice(-2) + ":"
          + ("00" + date.getMilliseconds()).slice(-3);

      await firestore.collection("TBAI").add({
        user: "1111111",
        type: "request",
        message: input,
        createTime: dateFormat,
        solved: "false"
      });

      setInput('');
    }
  };

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
      </InfiniteScroll>

      <Space.Compact style={{ width: "100%" }}>
        <Input
          defaultValue=""
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleEnterSubmit}
          placeholder="요청을 입력해주세요."
        />
        <Button type="primary" onClick={handleSubmit}>
          Submit
        </Button>
      </Space.Compact>
    </div>
  );
}

export default App;
