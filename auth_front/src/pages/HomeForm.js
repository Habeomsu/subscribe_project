import React from 'react';
import { useLogin } from '../contexts/AuthContext';

const HomeForm = () => {
  const { isLoggedIn, loginUser } = useLogin();
  return (
    <div>
      <h1>기본 회원가입, 로그인, 로그아웃 템플릿입니다. </h1>;
      <h2> {loginUser} 환영합니다</h2>
    </div>
  );
};

export default HomeForm;
