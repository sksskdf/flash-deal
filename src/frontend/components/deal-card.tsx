import { Deal } from '../lib/mock-data';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { ImageWithFallback } from './image-with-fallback';
import { PriceTag } from './price-tag';
import { CountdownTimer } from './countdown-timer';
import { StockBar } from './stock-bar';
import { ShoppingCart } from 'lucide-react@0.487.0';

interface DealCardProps {
  deal: Deal;
  onClick?: () => void;
  onBuyNow?: () => void;
}

export function DealCard({ deal, onClick, onBuyNow }: DealCardProps) {
  const statusConfig = {
    upcoming: { text: '곧 시작', color: 'var(--fd-info-600)', bg: 'var(--fd-info-50)' },
    active: { text: '진행중', color: 'var(--fd-success-600)', bg: 'var(--fd-success-50)' },
    soldout: { text: '품절', color: 'var(--fd-danger-600)', bg: 'var(--fd-danger-50)' },
    ended: { text: '종료', color: 'var(--fd-gray-600)', bg: 'var(--fd-gray-100)' },
  };

  const status = statusConfig[deal.status];

  return (
    <div
      className="bg-white rounded-xl overflow-hidden border cursor-pointer transition-all hover:shadow-lg"
      style={{ boxShadow: 'var(--fd-shadow-sm)', borderColor: 'var(--fd-border-default)' }}
      onClick={onClick}
    >
      {/* Image */}
      <div className="aspect-square bg-gray-100 relative overflow-hidden">
        <ImageWithFallback
          src={deal.image}
          alt={deal.title}
          className="w-full h-full object-cover"
        />
        {/* Status Badge */}
        <div className="absolute top-3 left-3">
          <Badge
            className="px-3 py-1"
            style={{
              backgroundColor: status.bg,
              color: status.color,
            }}
          >
            {status.text}
          </Badge>
        </div>
        {/* Discount Badge */}
        {deal.price.rate >= 50 && (
          <div className="absolute top-3 right-3">
            <Badge className="text-white" style={{ backgroundColor: 'var(--fd-danger-600)' }}>
              -{deal.price.rate}%
            </Badge>
          </div>
        )}
      </div>

      {/* Content */}
      <div className="p-4 space-y-3">
        {/* Title */}
        <h3 className="line-clamp-2 min-h-[3rem]" style={{ fontWeight: 600 }}>
          {deal.title}
        </h3>

        {/* Price */}
        <PriceTag original={deal.price.original} sale={deal.price.sale} />

        {/* Countdown */}
        {(deal.status === 'upcoming' || deal.status === 'active') && (
          <CountdownTimer
            targetTime={deal.status === 'upcoming' ? deal.startsAt : deal.endsAt}
            variant="compact"
          />
        )}

        {/* Stock */}
        {deal.status === 'active' && <StockBar level={deal.inventoryLevel} />}

        {/* Action Button */}
        <Button
          className="w-full"
          disabled={deal.status !== 'active'}
          onClick={(e) => {
            e.stopPropagation();
            onBuyNow?.();
          }}
          style={{
            backgroundColor:
              deal.status === 'active' ? 'var(--fd-primary-600)' : 'var(--fd-gray-300)',
          }}
        >
          <ShoppingCart className="w-4 h-4 mr-2" />
          {deal.status === 'active'
            ? '바로 구매'
            : deal.status === 'upcoming'
              ? '곧 시작'
              : deal.status === 'soldout'
                ? '품절'
                : '종료'}
        </Button>
      </div>
    </div>
  );
}

