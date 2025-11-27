import { useParams, useNavigate } from 'react-router-dom';
import { getDealById } from '../lib/mock-data';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { PriceTag } from '../components/price-tag';
import { CountdownTimer } from '../components/countdown-timer';
import { StockBar } from '../components/stock-bar';
import { useApp } from '../lib/app-context';
import { toast } from 'sonner@2.0.3';
import { ShoppingCart, Share2, Heart, ArrowLeft, CheckCircle2 } from 'lucide-react';
import { ImageWithFallback } from '../components/image-with-fallback';

export function DealDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart, user } = useApp();
  const deal = getDealById(id!);

  if (!deal) {
    return (
      <div className="min-h-screen bg-[var(--fd-bg-app)] flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl mb-2" style={{ fontWeight: 600 }}>딜을 찾을 수 없습니다</h2>
          <Button onClick={() => navigate('/deals')}>딜 둘러보기</Button>
        </div>
      </div>
    );
  }

  const handleBuyNow = () => {
    if (!user) {
      navigate('/auth', { state: { returnTo: `/deal/${deal.id}` } });
      return;
    }
    
    if (deal.status !== 'active') {
      toast.error('현재 이용할 수 없는 딜입니다');
      return;
    }

    addToCart(deal);
    navigate('/checkout');
  };

  const handleAddToCart = () => {
    if (!user) {
      navigate('/auth', { state: { returnTo: `/deal/${deal.id}` } });
      return;
    }
    
    if (deal.status !== 'active') {
      toast.error('현재 이용할 수 없는 딜입니다');
      return;
    }

    addToCart(deal);
    toast.success(`${deal.title}을(를) 장바구니에 담았습니다!`);
  };

  const statusConfig = {
    upcoming: { text: '곧 시작', color: 'var(--fd-info-600)', bg: 'var(--fd-info-50)' },
    active: { text: '진행중', color: 'var(--fd-success-600)', bg: 'var(--fd-success-50)' },
    soldout: { text: '품절', color: 'var(--fd-danger-600)', bg: 'var(--fd-danger-50)' },
    ended: { text: '종료', color: 'var(--fd-gray-600)', bg: 'var(--fd-gray-100)' },
  };

  const status = statusConfig[deal.status];

  return (
    <div className="min-h-screen bg-[var(--fd-bg-app)]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Button */}
        <Button 
          variant="ghost" 
          onClick={() => navigate(-1)}
          className="mb-6"
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          뒤로
        </Button>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-12">
          {/* Image Gallery */}
          <div className="space-y-4">
            <div 
              className="aspect-square rounded-xl overflow-hidden bg-white"
              style={{ boxShadow: 'var(--fd-shadow-md)' }}
            >
              <ImageWithFallback
                src={deal.image}
                alt={deal.title}
                className="w-full h-full object-cover"
              />
            </div>
          </div>

          {/* Product Info */}
          <div className="space-y-6">
            {/* Status Badge */}
            <div className="flex items-center gap-3 flex-wrap">
              <Badge 
                className="px-4 py-1.5"
                style={{ 
                  backgroundColor: status.bg,
                  color: status.color,
                }}
              >
                {status.text}
              </Badge>
              <Badge variant="outline">{deal.category}</Badge>
              {deal.price.rate >= 50 && (
                <Badge className="bg-[var(--fd-danger-600)] text-white">
                  핫딜 🔥
                </Badge>
              )}
            </div>

            {/* Title */}
            <div>
              <h1 className="text-3xl sm:text-4xl mb-2" style={{ fontWeight: 700 }}>
                {deal.title}
              </h1>
              {deal.subtitle && (
                <p className="text-lg text-[var(--fd-fg-muted)]">
                  {deal.subtitle}
                </p>
              )}
            </div>

            {/* Price */}
            <div className="p-6 bg-gradient-to-br from-[var(--fd-primary-50)] to-[var(--fd-info-50)] rounded-xl">
              <PriceTag
                original={deal.price.original}
                sale={deal.price.sale}
                emphasize
              />
            </div>

            {/* Countdown */}
            {(deal.status === 'upcoming' || deal.status === 'active') && (
              <CountdownTimer
                targetTime={deal.status === 'upcoming' ? deal.startsAt : deal.endsAt}
                variant="banner"
              />
            )}

            {/* Stock */}
            {deal.status === 'active' && (
              <div className="p-4 bg-white rounded-xl border border-[var(--fd-border-default)]">
                <StockBar level={deal.inventoryLevel} showExact />
              </div>
            )}

            {/* Actions */}
            <div className="space-y-3">
              <Button
                size="lg"
                className="w-full"
                disabled={deal.status !== 'active'}
                onClick={handleBuyNow}
                style={{
                  backgroundColor: deal.status === 'active' ? 'var(--fd-primary-600)' : 'var(--fd-gray-300)',
                }}
              >
                <ShoppingCart className="w-5 h-5 mr-2" />
                {deal.status === 'active' ? '바로 구매' : 
                 deal.status === 'upcoming' ? '아직 시작 전' :
                 deal.status === 'soldout' ? '품절' : '딜 종료'}
              </Button>
              
              {deal.status === 'active' && (
                <div className="grid grid-cols-2 gap-3">
                  <Button
                    variant="outline"
                    size="lg"
                    onClick={handleAddToCart}
                  >
                    장바구니에 담기
                  </Button>
                  <Button
                    variant="outline"
                    size="lg"
                  >
                    <Heart className="w-5 h-5 mr-2" />
                    저장
                  </Button>
                </div>
              )}
              
              <Button
                variant="outline"
                size="lg"
                className="w-full"
                onClick={() => {
                  navigator.clipboard.writeText(window.location.href);
                  toast.success('링크가 복사되었습니다!');
                }}
              >
                <Share2 className="w-5 h-5 mr-2" />
                공유하기
              </Button>
            </div>

            {/* Description */}
            {deal.description && (
              <div className="pt-6 border-t border-[var(--fd-border-default)]">
                <h3 className="text-lg mb-3" style={{ fontWeight: 600 }}>
                  상품 설명
                </h3>
                <p className="text-[var(--fd-fg-muted)] leading-relaxed">
                  {deal.description}
                </p>
              </div>
            )}

            {/* Specs */}
            {deal.specs && deal.specs.length > 0 && (
              <div className="pt-6 border-t border-[var(--fd-border-default)]">
                <h3 className="text-lg mb-4" style={{ fontWeight: 600 }}>
                  주요 특징
                </h3>
                <div className="space-y-2">
                  {deal.specs.map((spec, index) => (
                    <div key={index} className="flex items-center gap-3">
                      <CheckCircle2 className="w-5 h-5 text-[var(--fd-success-600)] flex-shrink-0" />
                      <span className="text-[var(--fd-fg-default)]">{spec}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
