import './App.css';
import { firestore } from "./Firebase";
import React, { useEffect, useState, useCallback } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Button, Input, Space, Divider, List, Skeleton } from 'antd';

function App() {

  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);

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
    .orderBy('order', 'asc')
    .get() // collection 하위 모든 document
    .then((docs) => {
      // forEach 함수로 각각의 다큐먼트에 함수 실행
      docs.forEach((doc) => {

        let docData = doc.data();

        firebaseData.push(
            { user: docData.user,
              type: docData.type,
              message: docData.message,
              responseType: docData.responseType,
              id: doc.id }
        );

        console.log(doc.data())
      });

      setData(firebaseData);
    });
  }, []);

  useEffect(() => {
    loadMoreData();
    fetchFirebase();
  }, [fetchFirebase]);

  return (
      <div
          id="scrollableDiv"
          style={{
            height: 400,
            overflow: 'auto',
            padding: '0 16px',
            border: '1px solid rgba(140, 140, 140, 0.35)',
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
          <List
              dataSource={data}
              renderItem={(item) => (
                  <List.Item key={null}>
                    <List.Item.Meta
                        title={item.type}
                        description={item.message}
                    />
                  </List.Item>
              )}
          />
        </InfiniteScroll>

        <Space.Compact style={{ width: '100%' }}>
          <Input defaultValue="" />
          <Button type="primary">Submit</Button>
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
