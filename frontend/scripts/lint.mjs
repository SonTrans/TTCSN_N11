import { readdir, readFile } from 'node:fs/promises';
async function files(dir){ return (await readdir(dir,{withFileTypes:true})).flatMap(e=>e.isDirectory()?[]:[`${dir}/${e.name}`]); }
const targets=[...(await files('src/api')),'src/app.js'];
for(const path of targets){ const text=await readFile(path,'utf8'); if(text.includes('\t')) throw new Error(`${path}: tabs are not allowed`); new Function(text.replace(/^import .*$/gm,'').replace(/^export /gm,'')); }
console.log(`Linted ${targets.length} JavaScript files`);
