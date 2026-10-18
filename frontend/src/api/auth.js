import { apiFetch } from './client.js';
export const login = payload => apiFetch('/auth/login',{method:'POST',body:JSON.stringify(payload)});
export const register = payload => apiFetch('/auth/register',{method:'POST',body:JSON.stringify(payload)});
export const fetchCurrentUser = () => apiFetch('/auth/me');
