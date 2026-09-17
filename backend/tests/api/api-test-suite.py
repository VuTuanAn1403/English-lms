#!/usr/bin/env python3
"""
==============================================================================
ENGLISH LMS - REST API TEST SUITE (Python unittest)
==============================================================================
Mục đích: Suite kiểm thử các Endpoint REST API khi hệ thống đang chạy
Cách chạy:
    python tests/api/api-test-suite.py
==============================================================================
"""

import unittest
import urllib.request
import json
import os
import sys

BASE_URL = os.environ.get("GATEWAY_URL", "http://localhost:8080").rstrip('/')

def post_json(url, data, token=None):
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers=headers, method='POST')
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.status, json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        body = e.read().decode('utf-8')
        try:
            return e.code, json.loads(body)
        except Exception:
            return e.code, {"raw": body}

def get_json(url, token=None):
    headers = {}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(url, headers=headers, method='GET')
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.status, json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        body = e.read().decode('utf-8')
        try:
            return e.code, json.loads(body)
        except Exception:
            return e.code, {"raw": body}

class TestEnglishLmsApi(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        cls.student_email = "api_student@gmail.com"
        cls.student_pass = "password123"
        cls.admin_email = "admin@gmail.com"
        cls.admin_pass = "admin123"

        # Register demo student if not exists
        post_json(f"{BASE_URL}/api/v1/auth/register", {
            "email": cls.student_email,
            "password": cls.student_pass,
            "fullName": "API Test Student",
            "role": "STUDENT"
        })

        # Login Student
        status, res = post_json(f"{BASE_URL}/api/v1/auth/login", {
            "email": cls.student_email,
            "password": cls.student_pass
        })
        cls.student_token = res.get("data", {}).get("token") or res.get("token")

        # Login Admin
        status, res = post_json(f"{BASE_URL}/api/v1/auth/login", {
            "email": cls.admin_email,
            "password": cls.admin_pass
        })
        cls.admin_token = res.get("data", {}).get("token") or res.get("token")

    def test_01_public_course_catalog(self):
        status, res = get_json(f"{BASE_URL}/api/v1/courses")
        self.assertEqual(status, 200)
        self.assertIn("data", res)

    def test_02_student_access_admin_revenue_returns_403(self):
        if self.student_token:
            status, res = get_json(f"{BASE_URL}/api/v1/admin/revenue", token=self.student_token)
            self.assertEqual(status, 403)

    def test_03_admin_access_admin_revenue_returns_200(self):
        if self.admin_token:
            status, res = get_json(f"{BASE_URL}/api/v1/admin/revenue", token=self.admin_token)
            self.assertEqual(status, 200)

if __name__ == "__main__":
    unittest.main()
