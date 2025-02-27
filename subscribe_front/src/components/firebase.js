import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

export const firebaseConfig = {
  apiKey: process.env.REACT_APP_API_KEY,
  authDomain: process.env.REACT_APP_AUTH_DOMAIN,
  projectId: process.env.REACT_APP_PROJECTID,
  storageBucket: process.env.REACT_APP_STORAGE_BUCKET,
  messagingSenderId: process.env.REACT_APP_MESSAGING_SENDER_ID,
  appId: process.env.REACT_APP_APPID,
  measurementId: process.env.REACT_APP_MEASUREMENTID,
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

if ('serviceWorker' in navigator) {
  navigator.serviceWorker
    .register('/firebase-messaging-sw.js')
    .then((registration) => {
      console.log('Service Worker 등록 성공:', registration);
    })
    .catch((error) => {
      console.error('Service Worker 등록 실패:', error);
    });
}

export const requestFCMToken = async () => {
  try {
    const permission = await Notification.requestPermission();
    if (permission === 'granted') {
      const token = await getToken(messaging, {
        vapidKey: process.env.REACT_APP_VAPID_KEY,
      });

      console.log('FCM 토큰:', token);
      return token;
    } else {
      console.log('알림 권한이 거부되었습니다.');
      return null;
    }
  } catch (error) {
    console.error('FCM 토큰 요청 실패:', error);
    return null;
  }
};

// ✅ 포그라운드 메시지 수신
onMessage(messaging, (payload) => {
  console.log('포그라운드 메시지 수신:', payload);

  const { title, body, image } = payload.notification;
  const notificationOptions = {
    body,
    icon: image || '/default-icon.png', // 기본 아이콘 설정 가능
  };

  // 클라이언트에서 알림 표시
  new Notification(title, notificationOptions);
});

export { app, messaging };
