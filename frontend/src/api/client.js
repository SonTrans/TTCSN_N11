const API_URL = localStorage.getItem('apiUrl') || 'http://localhost:8080/api';

export class ApiError extends Error {
  constructor(message,status,errors={}) { super(message); this.status=status; this.errors=errors; }
}

export async function apiFetch(path, options={}) {
  const headers = new Headers(options.headers || {}); const token=localStorage.getItem('accessToken');
  if(token) headers.set('Authorization',`Bearer ${token}`);
  if(options.body && !(options.body instanceof FormData)) headers.set('Content-Type','application/json');
  const response=await fetch(`${API_URL}${path}`,{...options,headers});
  if(response.status===401){ localStorage.removeItem('accessToken'); window.dispatchEvent(new Event('auth:expired')); }
  const data=response.status===204?null:await response.json().catch(()=>null);
  if(!response.ok) throw new ApiError(data?.detail || 'The server could not complete the request.',response.status,data?.errors);
  return data;
}
