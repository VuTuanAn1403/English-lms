import React, { useState, useEffect, useRef, useCallback } from 'react';
import {
  Box,
  TextField,
  IconButton,
  Paper,
  Typography,
  Tabs,
  Tab,
  CircularProgress,
  Avatar,
  Divider,
  Card,
  CardContent,
  Radio,
  RadioGroup,
  FormControlLabel,
  Button,
  Chip,
  Tooltip,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Alert
} from '@mui/material';
import {
  Send as SendIcon,
  SmartToy as AiIcon,
  Spellcheck as GrammarIcon,
  Quiz as QuizIcon,
  Person as PersonIcon,
  Refresh as RefreshIcon,
  School as TeacherIcon,
  AutoAwesome as SparklesIcon,
  CheckCircle as CorrectIcon,
  ErrorOutline as ErrorIcon,
  HelpOutline as HelpIcon,
  AddComment as NewChatIcon
} from '@mui/icons-material';
import api from '../services/api';

const ChatBox = () => {
  const [activeTab, setActiveTab] = useState(0); // 0: Chat, 1: Grammar, 2: Quiz
  const [messages, setMessages] = useState([
    {
      sender: 'ai',
      text: 'Chào bạn! Tôi là Giáo viên Tiếng Anh tại English LMS. Bạn đang ở trình độ nào và có mục tiêu học tập gì (ví dụ: IELTS 6.5, học giao tiếp, thi TOEIC hay học từ đầu)? Hãy chia sẻ để tôi đưa ra lộ trình phù hợp nhất nhé!',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  // Stable sessionId per conversation - persists across re-renders but resets on "new chat"
  const sessionIdRef = useRef(crypto.randomUUID());

  // Auto scroll and focus refs
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  // Grammar state
  const [grammarText, setGrammarText] = useState("He don't likes football.");
  const [grammarResult, setGrammarResult] = useState(null);
  const [grammarError, setGrammarError] = useState(null);

  // Quiz state
  const [quizTopic, setQuizTopic] = useState('Present Perfect');
  const [quizLevel, setQuizLevel] = useState('B1');
  const [quizSkill, setQuizSkill] = useState('Grammar');
  const [quizDifficulty, setQuizDifficulty] = useState('Intermediate');
  const [quizCount, setQuizCount] = useState(5);
  const [quizQuestions, setQuizQuestions] = useState([]);
  const [userAnswers, setUserAnswers] = useState({});
  const [quizSubmitted, setQuizSubmitted] = useState(false);
  const [quizError, setQuizError] = useState(null);

  // Auto Scroll function
  const scrollToBottom = useCallback((behavior = 'smooth') => {
    messagesEndRef.current?.scrollIntoView({ behavior });
  }, []);

  useEffect(() => {
    if (activeTab === 0) {
      scrollToBottom();
    }
  }, [messages, loading, activeTab, scrollToBottom]);

  // Focus input when tab changes or component mounts
  useEffect(() => {
    if (activeTab === 0 && inputRef.current) {
      inputRef.current.focus();
    }
  }, [activeTab]);

  // Suggestion Chips
  const suggestionChips = [
    { label: '🎯 Tôi IELTS 5.0', prompt: 'Tôi IELTS 5.0' },
    { label: '🗣️ Tôi muốn học phát âm', prompt: 'Tôi muốn học phát âm' },
    { label: '📚 Tôi nên học khóa nào?', prompt: 'Tôi nên học khóa nào trong hệ thống English LMS?' },
    { label: '✍️ Sửa lỗi: He don\'t likes football', prompt: 'He don\'t likes football.' },
    { label: '📝 Tạo quiz Present Perfect', prompt: 'Hãy tạo quiz về Present Perfect' }
  ];

  // Helper xử lý thông điệp lỗi chuẩn từ Backend & HTTP Statuses
  const parseErrorMessage = (err) => {
    const status = err.response?.status;
    const backendMsg = err.response?.data?.message;

    if (status === 400) {
      return backendMsg || 'Dữ liệu gửi đi không hợp lệ. Vui lòng kiểm tra lại.';
    }
    if (status === 401) {
      return 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
    }
    if (status === 403) {
      return 'Bạn không có quyền thực hiện thao tác này.';
    }
    if (status === 429) {
      return 'Bạn đã gửi quá nhiều yêu cầu. Vui lòng chờ một lát rồi thử lại.';
    }
    if (status === 502) {
      return 'AI trả về dữ liệu không đúng định dạng. Vui lòng thử lại.';
    }
    if (status === 503) {
      return 'Dịch vụ AI hiện không khả dụng. Vui lòng thử lại sau.';
    }
    if (status === 504) {
      return 'Kết nối tới AI quá thời gian. Vui lòng thử lại.';
    }
    if (backendMsg) {
      return backendMsg;
    }
    return err.message || 'Đã có lỗi xảy ra khi kết nối tới AI Service.';
  };

  const handleSendChat = async (overridePrompt) => {
    const promptToSend = typeof overridePrompt === 'string' ? overridePrompt : input;
    if (!promptToSend.trim() || loading) return;

    const userMsg = {
      sender: 'user',
      text: promptToSend,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    // Auto scroll & focus
    setTimeout(() => {
      scrollToBottom();
      if (inputRef.current) inputRef.current.focus();
    }, 50);

    try {
      const res = await api.post('/api/v1/ai/chat', {
        message: promptToSend,
        sessionId: sessionIdRef.current
      });
      const aiResponse = res.data?.data?.response || res.data?.data?.content || 'Rất tiếc, AI chưa thể phản hồi lúc này.';
      setMessages((prev) => [
        ...prev,
        {
          sender: 'ai',
          text: aiResponse,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }
      ]);
    } catch (err) {
      const errMsg = parseErrorMessage(err);
      setMessages((prev) => [
        ...prev,
        {
          sender: 'ai',
          text: errMsg,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          isError: true
        }
      ]);
    }

    setLoading(false);
    setTimeout(() => {
      scrollToBottom();
      if (inputRef.current) inputRef.current.focus();
    }, 100);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendChat();
    }
  };

  // "Cuộc trò chuyện mới" - creates a new session
  const handleNewChat = () => {
    sessionIdRef.current = crypto.randomUUID();
    setMessages([
      {
        sender: 'ai',
        text: 'Cuộc trò chuyện mới! Hãy cho tôi biết mục tiêu học tập tiếng Anh hôm nay của bạn!',
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      }
    ]);
    if (inputRef.current) inputRef.current.focus();
  };

  // "Làm mới" - clears session memory on backend + resets frontend
  const handleResetChat = async () => {
    const oldSessionId = sessionIdRef.current;
    sessionIdRef.current = crypto.randomUUID();
    setMessages([
      {
        sender: 'ai',
        text: 'Phiên trò chuyện đã được làm mới. Hãy cho tôi biết mục tiêu học tập tiếng Anh hôm nay của bạn!',
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      }
    ]);
    if (inputRef.current) inputRef.current.focus();

    // Clear memory on backend (fire-and-forget)
    try {
      await api.post(`/api/v1/ai/sessions/${oldSessionId}/clear`);
    } catch {
      // Ignore errors from clear - frontend is already reset
    }
  };

  const handleGrammarCheck = async () => {
    if (!grammarText.trim()) return;
    setLoading(true);
    setGrammarError(null);
    setGrammarResult(null);
    try {
      const res = await api.post('/api/v1/ai/grammar', { text: grammarText });
      const data = res.data?.data;
      if (data) {
        setGrammarResult(data);
      } else {
        setGrammarError('AI không trả về kết quả. Vui lòng thử lại.');
      }
    } catch (err) {
      const errMsg = parseErrorMessage(err);
      setGrammarError(errMsg);
      setGrammarResult(null);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateQuiz = async () => {
    if (!quizTopic.trim()) return;
    setLoading(true);
    setUserAnswers({});
    setQuizSubmitted(false);
    setQuizError(null);
    setQuizQuestions([]);
    try {
      const res = await api.post('/api/v1/ai/quiz', {
        lesson: quizTopic,
        cefrLevel: quizLevel,
        skillTarget: quizSkill,
        difficulty: quizDifficulty,
        numberOfQuestions: Number(quizCount)
      });
      const data = res.data?.data;
      if (data && Array.isArray(data) && data.length > 0) {
        setQuizQuestions(data);
      } else {
        setQuizError('AI không trả về câu hỏi nào. Vui lòng thử lại.');
      }
    } catch (err) {
      const errMsg = parseErrorMessage(err);
      setQuizError(errMsg);
      setQuizQuestions([]);
    } finally {
      setLoading(false);
    }
  };

  // Helper to format text lines
  const renderFormattedText = (text) => {
    if (!text) return null;
    return text.split('\n').map((line, i) => (
      <React.Fragment key={i}>
        {line}
        {i < text.split('\n').length - 1 && <br />}
      </React.Fragment>
    ));
  };

  return (
    <Paper
      elevation={0}
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        overflow: 'hidden',
        bgcolor: '#f8fafc',
        borderRadius: 0
      }}
    >
      {/* HEADER TABS & ACTIONS */}
      <Box
        sx={{
          flexShrink: 0,
          bgcolor: 'white',
          borderBottom: '1px solid #e2e8f0',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          px: { xs: 1, sm: 2 }
        }}
      >
        <Tabs
          value={activeTab}
          onChange={(e, val) => setActiveTab(val)}
          textColor="primary"
          indicatorColor="primary"
          variant="scrollable"
          scrollButtons="auto"
          sx={{
            minHeight: 56,
            '& .MuiTab-root': { fontWeight: 700, fontSize: '0.9rem', py: 1.5 }
          }}
        >
          <Tab icon={<TeacherIcon />} label="Hỏi Đáp AI & Lộ Trình" iconPosition="start" />
          <Tab icon={<GrammarIcon />} label="Sửa Ngữ Pháp" iconPosition="start" />
          <Tab icon={<QuizIcon />} label="Tạo Bài Quiz" iconPosition="start" />
        </Tabs>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Chip
            icon={<SparklesIcon sx={{ fontSize: 16 }} />}
            label="AI Teacher"
            color="primary"
            size="small"
            sx={{ fontWeight: 800, display: { xs: 'none', sm: 'inline-flex' } }}
          />
          {activeTab === 0 && (
            <>
              <Tooltip title="Cuộc trò chuyện mới">
                <IconButton size="small" onClick={handleNewChat} color="primary">
                  <NewChatIcon />
                </IconButton>
              </Tooltip>
              <Tooltip title="Làm mới & xóa ngữ cảnh">
                <IconButton size="small" onClick={handleResetChat} color="default">
                  <RefreshIcon />
                </IconButton>
              </Tooltip>
            </>
          )}
        </Box>
      </Box>

      {/* TAB 0: CHAT LAYOUT */}
      {activeTab === 0 && (
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            height: '100%',
            flex: 1,
            minHeight: 0,
            overflow: 'hidden',
            position: 'relative'
          }}
        >
          {/* MESSAGES SCROLL CONTAINER */}
          <Box
            className="chat-messages-container"
            sx={{
              flex: 1,
              overflowY: 'auto',
              overflowX: 'hidden',
              minHeight: 0,
              px: { xs: 1.5, sm: 3, md: 6 },
              py: 3,
              display: 'flex',
              flexDirection: 'column',
              gap: 2.5
            }}
          >
            {/* STARTER SUGGESTION CHIPS */}
            {messages.length <= 2 && (
              <Box sx={{ mb: 2, display: 'flex', flexDirection: 'column', gap: 1.5, className: 'animate-fade-in' }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700, letterSpacing: 0.5 }}>
                  💡 GỢI Ý CÂU HỎI THƯỜNG GẶP:
                </Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                  {suggestionChips.map((chip, idx) => (
                    <Chip
                      key={idx}
                      label={chip.label}
                      clickable
                      onClick={() => handleSendChat(chip.prompt)}
                      disabled={loading}
                      sx={{
                        bgcolor: 'white',
                        border: '1px solid #cbd5e1',
                        fontWeight: 600,
                        '&:hover': { bgcolor: 'rgba(79,70,229,0.08)', borderColor: 'primary.main' }
                      }}
                    />
                  ))}
                </Box>
              </Box>
            )}

            {/* MESSAGES LIST */}
            {messages.map((msg, index) => {
              const isUser = msg.sender === 'user';
              return (
                <Box
                  key={index}
                  className="animate-fade-in"
                  sx={{
                    display: 'flex',
                    gap: 1.5,
                    alignSelf: isUser ? 'flex-end' : 'flex-start',
                    maxWidth: isUser ? { xs: '88%', sm: '75%' } : { xs: '95%', sm: '88%' },
                    flexDirection: isUser ? 'row-reverse' : 'row'
                  }}
                >
                  <Avatar
                    sx={{
                      bgcolor: isUser ? 'primary.main' : 'secondary.main',
                      background: isUser
                        ? 'linear-gradient(135deg, #4F46E5 0%, #3B82F6 100%)'
                        : msg.isError
                          ? 'linear-gradient(135deg, #EF4444 0%, #DC2626 100%)'
                          : 'linear-gradient(135deg, #10B981 0%, #059669 100%)',
                      width: 38,
                      height: 38,
                      boxShadow: '0 4px 10px rgba(0,0,0,0.1)'
                    }}
                  >
                    {isUser ? <PersonIcon fontSize="small" /> : msg.isError ? <ErrorIcon fontSize="small" /> : <TeacherIcon fontSize="small" />}
                  </Avatar>

                  <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: isUser ? 'flex-end' : 'flex-start' }}>
                    <Paper
                      elevation={0}
                      sx={{
                        p: 2.2,
                        borderRadius: isUser ? '20px 20px 4px 20px' : '20px 20px 20px 4px',
                        background: isUser
                          ? 'linear-gradient(135deg, #4F46E5 0%, #3B82F6 100%)'
                          : msg.isError
                            ? '#FEF2F2'
                            : '#FFFFFF',
                        color: isUser ? '#FFFFFF' : msg.isError ? '#DC2626' : '#1e293b',
                        border: isUser ? 'none' : msg.isError ? '1px solid #FECACA' : '1px solid #e2e8f0',
                        boxShadow: isUser
                          ? '0 4px 14px rgba(79, 70, 229, 0.25)'
                          : '0 4px 16px rgba(15, 23, 42, 0.04)'
                      }}
                    >
                      <Typography
                        variant="body1"
                        className="chat-markdown"
                        sx={{
                          whiteSpace: 'pre-wrap',
                          lineHeight: 1.65,
                          fontSize: '0.95rem',
                          fontFamily: 'inherit'
                        }}
                      >
                        {renderFormattedText(msg.text)}
                      </Typography>
                    </Paper>

                    <Typography
                      variant="caption"
                      color="text.secondary"
                      sx={{ mt: 0.6, px: 1, fontSize: '0.72rem', opacity: 0.8 }}
                    >
                      {msg.timestamp}
                    </Typography>
                  </Box>
                </Box>
              );
            })}

            {/* AI LOADING TYPING INDICATOR */}
            {loading && (
              <Box sx={{ display: 'flex', gap: 1.5, alignSelf: 'flex-start', alignItems: 'center', my: 1 }}>
                <Avatar
                  sx={{
                    background: 'linear-gradient(135deg, #10B981 0%, #059669 100%)',
                    width: 38,
                    height: 38
                  }}
                >
                  <TeacherIcon fontSize="small" />
                </Avatar>
                <Paper
                  elevation={0}
                  sx={{
                    px: 2.5,
                    py: 1.5,
                    borderRadius: '20px 20px 20px 4px',
                    bgcolor: '#FFFFFF',
                    border: '1px solid #e2e8f0',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 1
                  }}
                >
                  <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 600, mr: 1 }}>
                    AI Giáo viên đang suy luận & soạn bài
                  </Typography>
                  <span className="typing-dot"></span>
                  <span className="typing-dot"></span>
                  <span className="typing-dot"></span>
                </Paper>
              </Box>
            )}

            {/* SCROLL ANCHOR */}
            <div ref={messagesEndRef} style={{ float: 'left', clear: 'both' }} />
          </Box>

          {/* FIXED STICKY INPUT CONTAINER AT BOTTOM */}
          <Box
            sx={{
              flexShrink: 0,
              position: 'sticky',
              bottom: 0,
              zIndex: 10,
              bgcolor: 'rgba(255, 255, 255, 0.95)',
              backdropFilter: 'blur(12px)',
              borderTop: '1px solid #e2e8f0',
              px: { xs: 2, sm: 3, md: 6 },
              py: 2
            }}
          >
            <Paper
              elevation={0}
              sx={{
                p: '2px 4px',
                display: 'flex',
                alignItems: 'center',
                borderRadius: 4,
                border: '2px solid #cbd5e1',
                bgcolor: 'white',
                '&:focus-within': { borderColor: 'primary.main', boxShadow: '0 0 0 3px rgba(79, 70, 229, 0.15)' }
              }}
            >
              <TextField
                fullWidth
                multiline
                maxRows={4}
                minRows={1}
                variant="standard"
                placeholder="Hỏi AI Giáo viên tiếng Anh... (Nhấn Enter để gửi, Shift+Enter để xuống dòng)"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                disabled={loading}
                inputRef={inputRef}
                InputProps={{
                  disableUnderline: true,
                  sx: { px: 2, py: 1, fontSize: '0.95rem' }
                }}
              />

              <IconButton
                color="primary"
                onClick={() => handleSendChat()}
                disabled={loading || !input.trim()}
                sx={{
                  p: 1.2,
                  mr: 0.5,
                  bgcolor: input.trim() && !loading ? 'primary.main' : 'action.disabledBackground',
                  color: input.trim() && !loading ? 'white' : 'action.disabled',
                  '&:hover': { bgcolor: 'primary.dark' }
                }}
              >
                {loading ? <CircularProgress size={22} color="inherit" /> : <SendIcon fontSize="small" />}
              </IconButton>
            </Paper>

            <Typography variant="caption" color="text.secondary" sx={{ display: 'block', textAlign: 'center', mt: 1, fontSize: '0.73rem', opacity: 0.7 }}>
              Hỗ trợ bởi Spring AI & Google Gemini
            </Typography>
          </Box>
        </Box>
      )}

      {/* TAB 1: GRAMMAR CHECKER */}
      {activeTab === 1 && (
        <Box sx={{ flex: 1, overflowY: 'auto', p: { xs: 2, sm: 4 }, display: 'flex', flexDirection: 'column', gap: 3 }}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #e2e8f0' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
              Sửa Lỗi Ngữ Pháp Tiếng Anh Chi Tiết
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2.5 }}>
              Nhập câu hoặc đoạn văn cần phân tích. AI sẽ chỉ ra lỗi sai cụ thể, lý do sửa, quy tắc ngữ pháp và ví dụ minh họa.
            </Typography>

            <TextField
              multiline
              rows={3}
              fullWidth
              placeholder="Ví dụ: He don't likes football and she go to school yesterday..."
              value={grammarText}
              onChange={(e) => setGrammarText(e.target.value)}
              disabled={loading}
              sx={{ mb: 2 }}
            />

            <Button
              variant="contained"
              color="primary"
              startIcon={<GrammarIcon />}
              onClick={handleGrammarCheck}
              disabled={loading || !grammarText.trim()}
              sx={{ borderRadius: 2, px: 3, py: 1 }}
            >
              {loading ? 'Đang phân tích ngữ pháp...' : 'Kiểm tra ngữ pháp'}
            </Button>
          </Paper>

          {grammarError && (
            <Alert severity="error" sx={{ whiteSpace: 'pre-line', borderRadius: 3, fontWeight: 600 }}>
              {grammarError}
            </Alert>
          )}

          {grammarResult && (
            <Card sx={{ borderRadius: 3, border: '1px solid #10B981', bgcolor: '#F0FDF4', boxShadow: '0 4px 20px rgba(16,185,129,0.08)' }}>
              <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, p: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: 'secondary.main', display: 'flex', alignItems: 'center', gap: 1 }}>
                    <CorrectIcon color="secondary" /> KẾT QUẢ KIỂM TRA NGỮ PHÁP
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    {grammarResult.score !== undefined && grammarResult.score !== null && (
                      <Chip label={`Điểm: ${grammarResult.score}/10`} color="primary" sx={{ fontWeight: 800 }} />
                    )}
                  </Box>
                </Box>

                <Divider />

                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>VĂN BẢN GỐC:</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 600, color: '#ef4444', mt: 0.5 }}>
                    {grammarResult.original}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>CÂU ĐÃ SỬA CHUẨN:</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 800, color: '#10B981', mt: 0.5 }}>
                    {grammarResult.corrected}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>GIẢI THÍCH CHI TIẾT:</Typography>
                  <Typography variant="body2" sx={{ color: '#1e293b', lineHeight: 1.65, mt: 0.5 }}>
                    {grammarResult.explanation}
                  </Typography>
                </Box>

                {grammarResult.errors && grammarResult.errors.length > 0 && (
                  <Box>
                    <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>CÁC LỖI CỤ THỂ:</Typography>
                    <Box component="ul" sx={{ pl: 2.5, mt: 0.5, color: '#ef4444' }}>
                      {grammarResult.errors.map((m, i) => (
                        <li key={i}><Typography variant="body2" sx={{ fontWeight: 600 }}>{m}</Typography></li>
                      ))}
                    </Box>
                  </Box>
                )}

                {grammarResult.grammarRules && grammarResult.grammarRules.length > 0 && (
                  <Box>
                    <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>QUY TẮC NGỮ PHÁP ÁP DỤNG:</Typography>
                    <Box component="ul" sx={{ pl: 2.5, mt: 0.5 }}>
                      {grammarResult.grammarRules.map((r, i) => (
                        <li key={i}><Typography variant="body2" sx={{ fontWeight: 600, color: '#4F46E5' }}>{r}</Typography></li>
                      ))}
                    </Box>
                  </Box>
                )}

                {grammarResult.examples && grammarResult.examples.length > 0 && (
                  <Box>
                    <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>VÍ DỤ TƯƠNG TỰ:</Typography>
                    <Box component="ul" sx={{ pl: 2.5, mt: 0.5 }}>
                      {grammarResult.examples.map((ex, i) => (
                        <li key={i}><Typography variant="body2">{ex}</Typography></li>
                      ))}
                    </Box>
                  </Box>
                )}

                {grammarResult.tips && (
                  <Box sx={{ p: 2, bgcolor: 'rgba(79,70,229,0.08)', borderRadius: 2, borderLeft: '4px solid #4F46E5' }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 800, color: 'primary.main' }}>
                      💡 MẸO GHI NHỚ DỄ THUỘC:
                    </Typography>
                    <Typography variant="body2" sx={{ mt: 0.5, fontWeight: 600 }}>
                      {grammarResult.tips}
                    </Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          )}
        </Box>
      )}

      {/* TAB 2: QUIZ GENERATOR */}
      {activeTab === 2 && (
        <Box sx={{ flex: 1, overflowY: 'auto', p: { xs: 2, sm: 4 }, display: 'flex', flexDirection: 'column', gap: 3 }}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #e2e8f0' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
              Tạo Bài Trắc Nghiệm Thông Minh (Quiz Generator)
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2.5 }}>
              Tự động sinh bộ câu hỏi trắc nghiệm tiếng Anh có ngữ cảnh thực tế với 4 lựa chọn kèm giải thích chi tiết.
            </Typography>

            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '2fr 1fr 1fr 1fr' }, gap: 2, mb: 2.5 }}>
              <TextField
                size="small"
                label="Chủ đề / Bài học"
                value={quizTopic}
                onChange={(e) => setQuizTopic(e.target.value)}
              />

              <FormControl size="small">
                <InputLabel>Trình độ CEFR</InputLabel>
                <Select value={quizLevel} label="Trình độ CEFR" onChange={(e) => setQuizLevel(e.target.value)}>
                  <MenuItem value="A1">A1 (Cơ bản)</MenuItem>
                  <MenuItem value="A2">A2 (Sơ cấp)</MenuItem>
                  <MenuItem value="B1">B1 (Trung cấp)</MenuItem>
                  <MenuItem value="B2">B2 (Trung cao cấp)</MenuItem>
                  <MenuItem value="C1">C1 (Cao cấp)</MenuItem>
                </Select>
              </FormControl>

              <FormControl size="small">
                <InputLabel>Mục tiêu kỹ năng</InputLabel>
                <Select value={quizSkill} label="Mục tiêu kỹ năng" onChange={(e) => setQuizSkill(e.target.value)}>
                  <MenuItem value="Grammar">Grammar (Ngữ pháp)</MenuItem>
                  <MenuItem value="Vocabulary">Vocabulary (Từ vựng)</MenuItem>
                  <MenuItem value="IELTS">IELTS Target</MenuItem>
                  <MenuItem value="TOEIC">TOEIC Target</MenuItem>
                </Select>
              </FormControl>

              <TextField
                size="small"
                type="number"
                label="Số câu hỏi"
                value={quizCount}
                onChange={(e) => setQuizCount(e.target.value)}
                inputProps={{ min: 5, max: 10 }}
              />
            </Box>

            <Button
              variant="contained"
              color="secondary"
              startIcon={<QuizIcon />}
              onClick={handleGenerateQuiz}
              disabled={loading || !quizTopic.trim()}
              sx={{ borderRadius: 2, px: 3, py: 1 }}
            >
              {loading ? 'Đang tạo bài Quiz...' : 'Tạo bài Quiz'}
            </Button>
          </Paper>

          {quizError && (
            <Alert severity="error" sx={{ whiteSpace: 'pre-line', borderRadius: 3, fontWeight: 600 }}>
              {quizError}
            </Alert>
          )}

          {quizQuestions.length > 0 && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              {quizQuestions.map((q, idx) => (
                <Card key={idx} variant="outlined" sx={{ borderRadius: 3, borderColor: '#e2e8f0', boxShadow: '0 4px 16px rgba(15,23,42,0.04)' }}>
                  <CardContent sx={{ p: 3 }}>
                    <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 2, color: 'primary.main' }}>
                      Câu {idx + 1}: {q.question}
                    </Typography>

                    <RadioGroup
                      value={userAnswers[idx] !== undefined ? String(userAnswers[idx]) : ''}
                      onChange={(e) => setUserAnswers({ ...userAnswers, [idx]: parseInt(e.target.value, 10) })}
                    >
                      {q.options?.map((opt, oIdx) => (
                        <FormControlLabel
                          key={oIdx}
                          value={String(oIdx)}
                          control={<Radio size="small" />}
                          label={<Typography variant="body2" sx={{ fontWeight: 600 }}>{opt}</Typography>}
                          sx={{
                            mb: 1,
                            p: 1,
                            borderRadius: 2,
                            bgcolor: userAnswers[idx] === oIdx ? 'rgba(79,70,229,0.06)' : 'transparent'
                          }}
                        />
                      ))}
                    </RadioGroup>

                    {userAnswers[idx] !== undefined && (() => {
                      const correctIdx = q.correctAnswerIndex;
                      const isCorrect = userAnswers[idx] === correctIdx;
                      return (
                        <Box
                          sx={{
                            mt: 2,
                            p: 2,
                            bgcolor: isCorrect ? '#F0FDF4' : '#FEF2F2',
                            borderRadius: 2.5,
                            border: isCorrect ? '1px solid #10B981' : '1px solid #EF4444'
                          }}
                        >
                          <Typography
                            variant="subtitle2"
                            sx={{
                              fontWeight: 800,
                              color: isCorrect ? '#10B981' : '#EF4444',
                              display: 'flex',
                              alignItems: 'center',
                              gap: 1
                            }}
                          >
                            {isCorrect ? (
                              <><CorrectIcon color="secondary" /> Chính xác!</>
                            ) : (
                              <><ErrorIcon color="error" /> Chưa đúng. Đáp án đúng là: {q.options?.[correctIdx]}</>
                            )}
                          </Typography>

                          {q.explanation && (
                            <Typography variant="body2" sx={{ mt: 1, color: '#334155', lineHeight: 1.6 }}>
                              <strong>Giải thích:</strong> {q.explanation}
                            </Typography>
                          )}
                        </Box>
                      );
                    })()}
                  </CardContent>
                </Card>
              ))}
            </Box>
          )}
        </Box>
      )}
    </Paper>
  );
};

export default ChatBox;
