import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useLogin } from '../../contexts/AuthContext';
import { requestFCMToken } from '../../components/firebase';

const LoginForm = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { setIsLoggedIn, setLoginUser, setRole } = useLogin();

  const prevUrl = location.state || '/';

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const fetchLogin = async (credentials) => {
    try {
      const apiUrl = process.env.REACT_APP_API_URL;
      const response = await fetch(`${apiUrl}/auth/login`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      if (response.ok) {
        alert('로그인 성공');

        const data = await response.json();
        const { result } = data;

        const accessToken = response.headers.get('Authorization');

        if (accessToken) {
          window.localStorage.setItem('access', accessToken.substring(7)); // "Bearer " 제거
        }

        window.localStorage.setItem('name', result.username);
        window.localStorage.setItem('role', result.role);

        setIsLoggedIn(true);
        setLoginUser(result.username);
        setRole(result.role);

        // 로그인 완료 후, 이전 요청이 존재하면 이전 요청으로 이동
        navigate(prevUrl, { replace: true });
      } else {
        // 오류 응답 처리
        const errorData = await response.json();
        alert(`로그인 실패: ${errorData.message || '로그인 실패'}`);
      }
    } catch (error) {
      console.log('error: ', error);
    }
  };

  const loginHandler = async (e) => {
    e.preventDefault();
    const fcmToken = await requestFCMToken();
    const credentials = { username, password, fcmToken };
    fetchLogin(credentials);
  };

  return (
    <div className="login">
      <h1>로그인</h1>
      <form method="post" onSubmit={loginHandler}>
        <p>
          <span className="label">아이디</span>
          <input
            className="input-class"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="username"
          />
        </p>
        <p>
          <span className="label">비밀번호</span>
          <input
            className="input-class"
            type="password"
            autoComplete="off"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="password"
          />
        </p>
        <input type="submit" value="로그인" className="form-btn" />
      </form>
    </div>
  );
};

export default LoginForm;
