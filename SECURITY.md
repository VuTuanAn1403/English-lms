# Security Policy

## Reporting Security Issues

If you discover a potential security vulnerability in English LMS, please **do not open a public issue**. Instead, notify the maintainers directly.

## Security Best Practices
- Never commit `.env` files or API secrets into source control.
- Ensure JWT secrets in production profile are strong random tokens (>256 bits).
- All sensitive operations (role management, enrollment admin, payment verification) require proper authorization headers (`Bearer <token>`).
