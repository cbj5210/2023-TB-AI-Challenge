import "./App.css";
import { firestore } from "./Firebase";
import React, { useEffect, useState, useCallback } from "react";
import InfiniteScroll from "react-infinite-scroll-component";
import { Button, Input, Space, Divider, List, Skeleton, Avatar } from "antd";

function App() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [input, setInput] = useState("");

  const loadMoreData = () => {
    /*if (loading) {
      return;
    }
    setLoading(true);
    fetch('https://randomuser.me/api/?results=10&inc=name,gender,email,nat,picture&noinfo')
    .then((res) => res.json())
    .then((body) => {
      setData([...data, ...body.results]);
      setLoading(false);
    })
    .catch(() => {
      setLoading(false);
    });*/
  };

  const fetchFirebase = useCallback(() => {
    // 받아온 데이터를 저장할 배열
    let firebaseData = [];

    firestore
      .collection("TBAI")
      .orderBy("order", "asc")
      .get() // collection 하위 모든 document
      .then((docs) => {
        // forEach 함수로 각각의 다큐먼트에 함수 실행
        docs.forEach((doc) => {
          let docData = doc.data();

          firebaseData.push({
            user: docData.user,
            type: docData.type,
            message: docData.message,
            responseType: docData.responseType,
            id: doc.id,
            title: docData.type === "request" ? "정주상" : "시크릿 T",
            avatarSrc:
              docData.type === "request"
                ? "../images/profile.png"
                : "../images/logo.png",
          });

          console.log(doc.data());
        });
        console.log(firebaseData);
        setData(firebaseData);
      });
  }, []);

  useEffect(() => {
    loadMoreData();
    fetchFirebase();
  }, [fetchFirebase]);

  const handleSubmit = async () => {
    console.log(input);
    //input 에 들어있는 값을 firestore 에 post
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
        endMessage={<Divider plain>It is all, nothing more 🤐</Divider>}
        scrollableTarget="scrollableDiv"
      >
        <div style={{ border: "1px solid" }}>
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
        </div>
      </InfiniteScroll>

      <Space.Compact style={{ width: "100%" }}>
        <Input
          defaultValue=""
          value={input}
          onChange={(e) => setInput(e.target.value)}
        />
        <Button type="primary" onClick={handleSubmit}>
          Submit
        </Button>
      </Space.Compact>
    </div>
  );
}

/*function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}*/

export default App;
