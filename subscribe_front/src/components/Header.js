import React from 'react';
import { Container, Nav, Navbar } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useLogin } from '../contexts/AuthContext';
import LogoutForm from '../pages/auth/LogoutForm';
import ResignForm from '../pages/auth/ResignForm';

const Header = () => {
  const { isLoggedIn } = useLogin();
  return (
    <>
      <Navbar bg="dark" data-bs-theme="dark">
        <Container>
          <Link to="/" className="navbar-brand">
            구독 서비스 토이 프로젝트
          </Link>
          <Nav className="ms-auto">
            {isLoggedIn && (
              <>
                <LogoutForm /> {/* 로그아웃 버튼 */}
              </>
            )}
            {isLoggedIn && (
              <>
                <ResignForm /> {/* 탈퇴 버튼 */}
              </>
            )}
            {!isLoggedIn && (
              <>
                <Link to="/join" className="nav-link">
                  회원가입
                </Link>
                <Link to="/login" className="nav-link">
                  로그인
                </Link>
              </>
            )}
          </Nav>
        </Container>
      </Navbar>
    </>
  );
};

export default Header;
