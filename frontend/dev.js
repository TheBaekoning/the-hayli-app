import process from 'process';
import { spawn, exec } from 'child_process';

const dbProc = exec('docker compose up', { cwd: '../docker/local-detached' });
const mvnProc = exec('mvn spring-boot:run', { cwd: '../backend' });
const mvnZrok = spawn('zrok', ['share', 'reserved', 'haylihealthapi']);
const frontendProc = spawn('npx', ['vite'], { cwd: '../frontend' });
const frontendZrok = spawn('zrok', ['share', 'reserved', 'haylihealth']);

const procs = {dbProc, mvnProc, mvnZrok, frontendProc, frontendZrok};

// Pass all messages and errors to the console
function log(proc){
	proc.stdout.on('data', data => console.log(data.toString()));
	proc.stderr.on('data', data => console.error(data.toString()));
}
for(let proc in procs) log(procs[proc]);

process.on('SIGINT', () => {
	for(let proc in procs) procs[proc].kill('SIGINT');
});
