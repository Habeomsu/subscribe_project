import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const JoinForm = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [authNum, setAuthNum] = useState('');
  const [isVerified, setIsVerified] = useState(false); // 인증 여부

  const apiUrl = process.env.REACT_APP_API_URL;

  // 이메일로 인증번호 요청
  const sendVerificationCode = async () => {
    if (!email) {
      alert('이메일을 입력해 주세요.');
      return;
    }
    try {
      const response = await fetch(`${apiUrl}/auth/email/signup`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });
      if (response.ok) {
        alert('인증번호가 이메일로 전송되었습니다.');
      } else {
        const errorData = await response.json();
        alert(
          `인증번호 전송 실패: ${errorData.message || '알 수 없는 오류 발생'}`
        );
      }
    } catch (error) {
      console.log('Error:', error);
    }
  };

  // 인증번호 확인 요청
  const verifyCode = async () => {
    if (!authNum) {
      alert('인증번호를 입력해 주세요.');
      return;
    }
    try {
      const response = await fetch(`${apiUrl}/auth/email/checking`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, authNum }),
      });
      if (response.ok) {
        alert('이메일 인증 완료!');
        setIsVerified(true);
      } else {
        const errorData = await response.json();
        alert(
          `인증 실패: ${errorData.message || '올바른 인증번호를 입력하세요.'}`
        );
      }
    } catch (error) {
      console.log('Error:', error);
    }
  };

  // 회원가입 요청
  const fetchJoin = async (credentials) => {
    try {
      const response = await fetch(`${apiUrl}/auth/join`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
      });
      if (response.ok) {
        alert('회원가입 성공');
        navigate('/login', { replace: true });
      } else {
        const errorData = await response.json();
        alert(`회원가입 실패: ${errorData.message || '알 수 없는 오류 발생'}`);
      }
    } catch (error) {
      console.log('Error:', error);
    }
  };

  // 회원가입 핸들러
  const joinHandler = async (e) => {
    e.preventDefault();
    if (!isVerified) {
      alert('이메일 인증을 완료해 주세요.');
      return;
    }
    if (!username || !password) {
      alert('아이디와 비밀번호를 모두 입력해 주세요.');
      return;
    }
    const credentials = { email, username, password };
    fetchJoin(credentials);
  };

  return (
    <div className="join">
      <h1>회원가입</h1>
      {/* 이메일 입력 및 인증 */}
      <p>
        <span className="label">Email</span>
        <input
          className="input-class"
          type="email"
          value={email}
          placeholder="이메일 입력"
          onChange={(e) => setEmail(e.target.value)}
          disabled={isVerified} // 인증 완료 시 수정 불가
        />
        <button
          type="button"
          onClick={sendVerificationCode}
          disabled={isVerified}
        >
          인증번호 전송
        </button>
      </p>

      {/* 인증번호 입력 */}
      <p>
        <span className="label">인증번호</span>
        <input
          className="input-class"
          type="text"
          value={authNum}
          placeholder="인증번호 입력"
          onChange={(e) => setAuthNum(e.target.value)}
          disabled={isVerified} // 인증 완료 시 수정 불가
        />
        <button type="button" onClick={verifyCode} disabled={isVerified}>
          인증하기
        </button>
      </p>

      {/* 회원가입 입력 폼 (이메일 인증 후 활성화) */}
      <form onSubmit={joinHandler}>
        <p>
          <span className="label">Username</span>
          <input
            className="input-class"
            type="text"
            value={username}
            placeholder="아이디 입력"
            onChange={(e) => setUsername(e.target.value)}
            disabled={!isVerified} // 인증 완료 후 활성화
          />
        </p>
        <p>
          <span className="label">Password</span>
          <input
            className="input-class"
            type="password"
            autoComplete="off"
            placeholder="비밀번호 입력"
            onChange={(e) => setPassword(e.target.value)}
            disabled={!isVerified} // 인증 완료 후 활성화
          />
        </p>
        <input
          type="submit"
          value="회원가입"
          className="form-btn"
          disabled={!isVerified}
        />
      </form>
    </div>
  );
};

export default JoinForm;
