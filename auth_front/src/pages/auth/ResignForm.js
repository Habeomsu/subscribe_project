import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useLogin } from '../../contexts/AuthContext';
import FetchAuthorizedPage from '../../service/FetchAuthorizedPage';

const ResignForm = () => {
  const navigate = useNavigate();
  const { setIsLoggedIn, setLoginUser, setRole } = useLogin();

  const fetchWithdraw = async () => {
    try {
      const apiUrl = process.env.REACT_APP_API_URL;
      const response = await FetchAuthorizedPage(
        `${apiUrl}/auth/resign`,
        navigate,
        window.location,
        'DELETE'
      );

      if (response) {
        console.log('Response Data:', response);
        alert('탈퇴가 완료되었습니다. 안녕히 가세요.');

        window.localStorage.removeItem('access');
        window.localStorage.removeItem('name');
        window.localStorage.removeItem('role');

        setIsLoggedIn(false);
        setLoginUser(null);
        setRole(null);
        navigate('/', { replace: true }); // 홈으로 리디렉션
      } else {
        alert('탈퇴 실패');
      }
    } catch (error) {
      console.log('Error: ', error);
    }
  };

  const handleWithdrawClick = () => {
    // 탈퇴 확인 메시지
    const confirmed = window.confirm('정말 탈퇴하시겠습니까?');
    if (confirmed) {
      fetchWithdraw(); // 확인 후 탈퇴 요청
    }
  };

  return (
    <button
      onClick={handleWithdrawClick}
      className="nav-link"
      style={{
        background: 'none',
        border: 'none',
        color: 'white',
        cursor: 'pointer',
      }}
    >
      탈퇴하기
    </button>
  );
};

export default ResignForm;
