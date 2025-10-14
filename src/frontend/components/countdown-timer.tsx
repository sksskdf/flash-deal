import { useState, useEffect } from 'react';
import { Clock } from 'lucide-react@0.487.0';

interface CountdownTimerProps {
  targetTime: Date;
  variant?: 'banner' | 'compact';
}

export function CountdownTimer({ targetTime, variant = 'compact' }: CountdownTimerProps) {
  const [timeLeft, setTimeLeft] = useState(calculateTimeLeft());

  function calculateTimeLeft() {
    const diff = targetTime.getTime() - Date.now();
    if (diff <= 0) return { days: 0, hours: 0, minutes: 0, seconds: 0 };

    return {
      days: Math.floor(diff / (1000 * 60 * 60 * 24)),
      hours: Math.floor((diff / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((diff / (1000 * 60)) % 60),
      seconds: Math.floor((diff / 1000) % 60),
    };
  }

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft());
    }, 1000);

    return () => clearInterval(timer);
  }, [targetTime]);

  if (variant === 'banner') {
    return (
      <div className="bg-white/10 backdrop-blur-sm rounded-xl p-6">
        <div className="flex items-center justify-center gap-2 mb-4">
          <Clock className="w-5 h-5" />
          <span className="text-lg" style={{ fontWeight: 600 }}>
            {timeLeft.days + timeLeft.hours + timeLeft.minutes + timeLeft.seconds === 0
              ? '딜 종료'
              : '남은 시간'}
          </span>
        </div>
        <div className="grid grid-cols-4 gap-3 text-center">
          {Object.entries(timeLeft).map(([unit, value]) => {
            const unitKo = { days: '일', hours: '시간', minutes: '분', seconds: '초' }[unit] || unit;
            return (
              <div key={unit} className="bg-white/20 rounded-lg p-3">
                <div className="text-3xl" style={{ fontWeight: 700 }}>
                  {String(value).padStart(2, '0')}
                </div>
                <div className="text-sm mt-1">{unitKo}</div>
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  return (
    <div className="flex items-center gap-2 text-sm">
      <Clock className="w-4 h-4" style={{ color: 'var(--fd-fg-muted)' }} />
      <span style={{ color: 'var(--fd-fg-muted)' }}>
        {timeLeft.days > 0 && `${timeLeft.days}d `}
        {String(timeLeft.hours).padStart(2, '0')}:
        {String(timeLeft.minutes).padStart(2, '0')}:
        {String(timeLeft.seconds).padStart(2, '0')}
      </span>
    </div>
  );
}

