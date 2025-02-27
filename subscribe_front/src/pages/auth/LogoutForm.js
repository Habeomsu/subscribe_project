import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useLogin } from '../../contexts/AuthContext';
import { requestFCMToken } from '../../components/firebase';

const LogoutForm = () => {
  const navigate = useNavigate();
  const { setIsLoggedIn, setLoginUser, setRole } = useLogin();

  const fetchLogout = async (fcmToken) => {
    try {
      const apiUrl = process.env.REACT_APP_API_URL;
      const response = await fetch(`${apiUrl}/auth/logout`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ fcmToken }),
      });

      if (response.ok) {
        alert('로그아웃 성공! 안녕히 가세요.');
        window.localStorage.removeItem('access');
        window.localStorage.removeItem('name');
        window.localStorage.removeItem('role');

        setIsLoggedIn(false);
        setLoginUser(null);
        setRole(null);
        navigate('/', { replace: true }); // 홈으로 리디렉션
      } else {
        console.log(response.json());
        alert('로그아웃 실패');
      }
    } catch (error) {
      console.log('Error: ', error);
    }
  };

  const handleLogoutClick = async () => {
    // 로그아웃 확인 메시지
    const confirmed = window.confirm('정말 로그아웃하시겠습니까?');
    if (confirmed) {
      const fcmToken = await requestFCMToken(); // FCM 토큰 요청
      fetchLogout(fcmToken); // 확인 후 로그아웃 요청
    }
  };

  return (
    <button
      onClick={handleLogoutClick}
      className="nav-link"
      style={{
        background: 'none',
        border: 'none',
        color: 'white',
        cursor: 'pointer',
      }}
    >
      로그아웃
    </button>
  );
};

export default LogoutForm;
