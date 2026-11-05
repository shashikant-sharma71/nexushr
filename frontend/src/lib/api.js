// In production set VITE_API_URL to the deployed backend URL ending in /api.
// Never fall back to localhost in a public build.
const API = (import.meta.env.VITE_API_URL || (import.meta.env.DEV ? 'http://localhost:8080/api' : '/api')).replace(/\/$/, '');

export async function api(path, options = {}) {
  const token = localStorage.getItem('nexushr_token');
  const headers = { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;
  let response;
  try {
    response = await fetch(`${API}${path}`, { ...options, headers });
  } catch {
    const message = 'Cannot reach NexusHR API. Check that the backend is running.';
    window.dispatchEvent(new CustomEvent('nexushr:error', { detail: message }));
    throw new Error(message);
  }
  if (response.status === 401) {
    localStorage.removeItem('nexushr_token');
    localStorage.removeItem('nexushr_user');
    window.dispatchEvent(new Event('nexushr:unauthorized'));
    throw new Error('Your session expired. Please sign in again.');
  }
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try { const payload = await response.json(); message = payload.message || message; } catch { /* non-JSON error response */ }
    window.dispatchEvent(new CustomEvent('nexushr:error', { detail: message }));
    throw new Error(message);
  }
  if (response.status === 204) return null;
  const type = response.headers.get('content-type') || '';
  return type.includes('application/json') ? response.json() : response.text();
}
