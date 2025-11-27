import { InventoryLevel } from '../lib/mock-data';
import { Package } from 'lucide-react@0.487.0';

interface StockBarProps {
  level: InventoryLevel;
  showExact?: boolean;
}

export function StockBar({ level, showExact }: StockBarProps) {
  const config = {
    high: { percentage: 75, color: 'var(--fd-success-600)', bg: 'var(--fd-success-50)', text: '재고 충분' },
    mid: { percentage: 40, color: 'var(--fd-warning-600)', bg: 'var(--fd-warning-50)', text: '재고 부족' },
    low: { percentage: 15, color: 'var(--fd-danger-600)', bg: 'var(--fd-danger-50)', text: '품절 임박!' },
  };

  const stock = config[level];

  return (
    <div className="space-y-2">
      {showExact && (
        <div className="flex items-center gap-2 text-sm">
          <Package className="w-4 h-4" style={{ color: stock.color }} />
          <span style={{ fontWeight: 500, color: stock.color }}>
            {stock.text}
          </span>
        </div>
      )}
      <div className="relative h-2 rounded-full overflow-hidden" style={{ backgroundColor: stock.bg }}>
        <div
          className="absolute inset-y-0 left-0 rounded-full transition-all"
          style={{
            width: `${stock.percentage}%`,
            backgroundColor: stock.color,
          }}
        />
      </div>
      {!showExact && (
        <p className="text-xs" style={{ color: 'var(--fd-fg-muted)' }}>
          {stock.text}
        </p>
      )}
    </div>
  );
}

