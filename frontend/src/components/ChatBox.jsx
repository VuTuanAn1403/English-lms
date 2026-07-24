import React, { useState } from 'react';
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
  Button
} from '@mui/material';
import {
  Send as SendIcon,
  SmartToy as AiIcon,
  Spellcheck as GrammarIcon,
  Quiz as QuizIcon,
  Person as PersonIcon,
  CheckCircle as CorrectIcon
} from '@mui/icons-material';
import api from '../services/api';

const ChatBox = () => {
  const [activeTab, setActiveTab] = useState(0); // 0: Chat, 1: Grammar, 2: Quiz
  const [messages, setMessages] = useState([
    {
      sender: 'ai',
      text: 'Xin chào! Tôi là Trợ lý AI học tiếng Anh. Bạn có thể trò chuyện, nhờ tôi sửa lỗi ngữ pháp hoặc tạo bài trắc nghiệm!',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  // Grammar state
  const [grammarText, setGrammarText] = useState('');
  const [grammarResult, setGrammarResult] = useState(null);

  // Quiz state
  const [quizTopic, setQuizTopic] = useState('Present Simple');
  const [quizCount, setQuizCount] = useState(5);
  const [quizQuestions, setQuizQuestions] = useState([]);
  const [userAnswers, setUserAnswers] = useState({});

  const handleSendChat = async () => {
    if (!input.trim()) return;

    const userMsg = {
      sender: 'user',
      text: input,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };
    setMessages((prev) => [...prev, userMsg]);
    const prompt = input;
    setInput('');
    setLoading(true);

    try {
      const res = await api.post('/api/v1/ai/chat', { message: prompt });
      const aiResponse = res.data?.data?.response || 'Rất tiếc, AI chưa thể phản hồi lúc này.';
      setMessages((prev) => [
        ...prev,
        {
          sender: 'ai',
          text: aiResponse,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }
      ]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        {
          sender: 'ai',
          text: 'Đã có lỗi xảy ra khi kết nối tới AI Service.',
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleGrammarCheck = async () => {
    if (!grammarText.trim()) return;
    setLoading(true);
    try {
      const res = await api.post('/api/v1/ai/grammar', { text: grammarText });
      setGrammarResult(res.data?.data || null);
    } catch (err) {
      setGrammarResult({
        original: grammarText,
        corrected: grammarText,
        explanation: 'Không thể kết nối tới AI Service để kiểm tra ngữ pháp.'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateQuiz = async () => {
    if (!quizTopic.trim()) return;
    setLoading(true);
    setUserAnswers({});
    try {
      const res = await api.post('/api/v1/ai/quiz', {
        lesson: quizTopic,
        numberOfQuestions: Number(quizCount)
      });
      setQuizQuestions(res.data?.data || []);
    } catch (err) {
      setQuizQuestions([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Paper className="glass-card" sx={{ borderRadius: 4, overflow: 'hidden', height: 600, display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ borderBottom: 1, borderColor: 'divider', bgcolor: 'primary.main', color: 'white' }}>
        <Tabs
          value={activeTab}
          onChange={(e, val) => setActiveTab(val)}
          textColor="inherit"
          indicatorColor="secondary"
          variant="fullWidth"
        >
          <Tab icon={<AiIcon />} label="Hỏi đáp AI" iconPosition="start" />
          <Tab icon={<GrammarIcon />} label="Sửa ngữ pháp" iconPosition="start" />
          <Tab icon={<QuizIcon />} label="Tạo bài Quiz" iconPosition="start" />
        </Tabs>
      </Box>

      {/* TAB 0: CHAT AI */}
      {activeTab === 0 && (
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', p: 2 }}>
          <Box sx={{ flexGrow: 1, overflowY: 'auto', pr: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
            {messages.map((msg, index) => (
              <Box
                key={index}
                sx={{
                  display: 'flex',
                  gap: 1.5,
                  alignSelf: msg.sender === 'user' ? 'flex-end' : 'flex-start',
                  maxWidth: '80%',
                  flexDirection: msg.sender === 'user' ? 'row-reverse' : 'row'
                }}
              >
                <Avatar sx={{ bgcolor: msg.sender === 'user' ? 'secondary.main' : 'primary.main', width: 36, height: 36 }}>
                  {msg.sender === 'user' ? <PersonIcon /> : <AiIcon />}
                </Avatar>
                <Box>
                  <Paper
                    sx={{
                      p: 2,
                      borderRadius: 3,
                      bgcolor: msg.sender === 'user' ? 'primary.main' : '#FFFFFF',
                      color: msg.sender === 'user' ? 'white' : 'text.primary',
                      boxShadow: '0 2px 10px rgba(0,0,0,0.05)'
                    }}
                  >
                    <Typography variant="body2" sx={{ whitespace: 'pre-wrap', lineHeight: 1.6 }}>
                      {msg.text}
                    </Typography>
                  </Paper>
                  <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5, textAlign: msg.sender === 'user' ? 'right' : 'left' }}>
                    {msg.timestamp}
                  </Typography>
                </Box>
              </Box>
            ))}
            {loading && (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, alignSelf: 'flex-start' }}>
                <Avatar sx={{ bgcolor: 'primary.main', width: 36, height: 36 }}><AiIcon /></Avatar>
                <CircularProgress size={24} color="primary" />
              </Box>
            )}
          </Box>

          <Divider sx={{ my: 2 }} />

          <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
              fullWidth
              size="small"
              placeholder="Nhập câu hỏi tiếng Anh hoặc thắc mắc của bạn..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSendChat()}
              disabled={loading}
              sx={{ bgcolor: 'white', borderRadius: 2 }}
            />
            <IconButton color="primary" onClick={handleSendChat} disabled={loading || !input.trim()}>
              <SendIcon />
            </IconButton>
          </Box>
        </Box>
      )}

      {/* TAB 1: GRAMMAR CHECKER */}
      {activeTab === 1 && (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 2.5, overflowY: 'auto' }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
            Nhập câu tiếng Anh cần kiểm tra và sửa lỗi ngữ pháp:
          </Typography>
          <TextField
            multiline
            rows={3}
            fullWidth
            placeholder="Ví dụ: I has a book and she go to school yesterday..."
            value={grammarText}
            onChange={(e) => setGrammarText(e.target.value)}
            disabled={loading}
          />
          <Button
            variant="contained"
            color="primary"
            startIcon={<GrammarIcon />}
            onClick={handleGrammarCheck}
            disabled={loading || !grammarText.trim()}
            sx={{ alignSelf: 'flex-start' }}
          >
            {loading ? 'Đang kiểm tra...' : 'Kiểm tra ngữ pháp'}
          </Button>

          {grammarResult && (
            <Card sx={{ bgcolor: 'rgba(16, 185, 129, 0.05)', borderColor: 'secondary.main' }}>
              <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                <Box>
                  <Typography variant="caption" color="text.secondary">Văn bản gốc:</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 600, color: 'error.main' }}>{grammarResult.original}</Typography>
                </Box>
                <Box>
                  <Typography variant="caption" color="text.secondary">Câu đã sửa chuẩn:</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 700, color: 'secondary.main' }}>{grammarResult.corrected}</Typography>
                </Box>
                <Box>
                  <Typography variant="caption" color="text.secondary">Giải thích bằng tiếng Việt:</Typography>
                  <Typography variant="body2" sx={{ color: 'text.primary', lineHeight: 1.6 }}>{grammarResult.explanation}</Typography>
                </Box>
              </CardContent>
            </Card>
          )}
        </Box>
      )}

      {/* TAB 2: QUIZ GENERATOR */}
      {activeTab === 2 && (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 2.5, overflowY: 'auto' }}>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
            <TextField
              size="small"
              label="Chủ đề / Bài học"
              value={quizTopic}
              onChange={(e) => setQuizTopic(e.target.value)}
              sx={{ flexGrow: 1 }}
            />
            <TextField
              size="small"
              type="number"
              label="Số câu hỏi"
              value={quizCount}
              onChange={(e) => setQuizCount(e.target.value)}
              sx={{ width: 120 }}
            />
            <Button
              variant="contained"
              color="secondary"
              startIcon={<QuizIcon />}
              onClick={handleGenerateQuiz}
              disabled={loading || !quizTopic.trim()}
            >
              {loading ? 'Đang tạo...' : 'Tạo bài Quiz'}
            </Button>
          </Box>

          <Divider />

          {quizQuestions.length > 0 && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              {quizQuestions.map((q, idx) => (
                <Card key={idx} variant="outlined" sx={{ borderRadius: 3 }}>
                  <CardContent>
                    <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1.5 }}>
                      Câu {idx + 1}: {q.question}
                    </Typography>
                    <RadioGroup
                      value={userAnswers[idx] || ''}
                      onChange={(e) => setUserAnswers({ ...userAnswers, [idx]: e.target.value })}
                    >
                      {q.options?.map((opt, oIdx) => (
                        <FormControlLabel key={oIdx} value={opt} control={<Radio size="small" />} label={opt} />
                      ))}
                    </RadioGroup>

                    {userAnswers[idx] && (
                      <Box sx={{ mt: 1.5, p: 1.5, bgcolor: userAnswers[idx] === q.answer ? 'rgba(16,185,129,0.1)' : 'rgba(239,68,68,0.1)', borderRadius: 2 }}>
                        <Typography variant="body2" sx={{ fontWeight: 700, color: userAnswers[idx] === q.answer ? 'secondary.main' : 'error.main' }}>
                          {userAnswers[idx] === q.answer ? '✓ Chính xác!' : `✗ Chưa đúng. Đáp án đúng: ${q.answer}`}
                        </Typography>
                        {q.explanation && (
                          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
                            Giải thích: {q.explanation}
                          </Typography>
                        )}
                      </Box>
                    )}
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
