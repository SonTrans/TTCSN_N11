import { createServer } from 'node:http';
import { readFile, stat } from 'node:fs/promises';
import { extname, join, normalize } from 'node:path';

const root = process.argv[2] || 'src';
const port = Number(process.argv[3] || 5173);
const types = { '.html':'text/html; charset=utf-8', '.css':'text/css; charset=utf-8', '.js':'text/javascript; charset=utf-8', '.svg':'image/svg+xml' };
createServer(async (request,response) => {
  try {
    const urlPath = decodeURIComponent(new URL(request.url, 'http://local').pathname);
    const safe = normalize(urlPath).replace(/^(\.\.(\/|\\|$))+/, '');
    let path = join(root, safe === '/' ? 'index.html' : safe);
    if ((await stat(path)).isDirectory()) path = join(path, 'index.html');
    response.writeHead(200, {'Content-Type':types[extname(path)] || 'application/octet-stream'}); response.end(await readFile(path));
  } catch { response.writeHead(404); response.end('Not found'); }
}).listen(port, () => console.log(`CKD frontend: http://localhost:${port}`));
