import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = { vus: 1000, duration: '15m' };
export default function() {
    const res = http.get('http://app:8080/test');
    check(res, { '200 OK': r => r.status === 200 });
    sleep(1);
}