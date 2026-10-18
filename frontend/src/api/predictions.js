import { apiFetch } from './client.js';
export const createPrediction = payload => apiFetch('/predictions',{method:'POST',body:JSON.stringify(payload)});
export const createBatchPrediction = file => { const body=new FormData(); body.append('file',file); return apiFetch('/predictions/batch',{method:'POST',body}); };
export const fetchPredictions = (page=0,size=20) => apiFetch(`/predictions?page=${page}&size=${size}`);
export const fetchPredictionSummary = () => apiFetch('/predictions/summary');
