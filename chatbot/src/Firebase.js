import firebase from "firebase/compat/app"
import 'firebase/compat/firestore';

const firebaseConfig = {
  apiKey: "AIzaSyBaYjtl7zisyT1ACIlW3No8wYYZDBIBTNw",
  authDomain: "trim-mile-404501.firebaseapp.com",
  projectId: "trim-mile-404501",
  storageBucket: "trim-mile-404501.appspot.com",
  messagingSenderId: "630056211759",
  appId: "1:630056211759:web:c823bb5009d0c8ef117dda",
  measurementId: "G-BWZXZ2SXRL"
};

firebase.initializeApp(firebaseConfig);
const firestore = firebase.firestore();

export { firestore };