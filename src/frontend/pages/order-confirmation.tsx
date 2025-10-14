import { useParams, useNavigate } from 'react-router-dom';
import { useApp } from '../lib/app-context';
import { Button } from '../components/ui/button';
import { Card } from '../components/ui/card';
import { CheckCircle2, Package, ArrowRight } from 'lucide-react';
import { ImageWithFallback } from '../components/image-with-fallback';

export function OrderConfirmationPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { orders } = useApp();

  const order = orders.find(o => o.id === id);

  if (!order) {
    return (
      <div className="min-h-screen bg-[var(--fd-bg-app)] flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl mb-2" style={{ fontWeight: 600 }}>주문을 찾을 수 없습니다</h2>
          <Button onClick={() => navigate('/deals')}>딜 둘러보기</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[var(--fd-bg-app)]">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        {/* Success Header */}
        <div className="text-center mb-12">
          <div className="w-20 h-20 rounded-full bg-[var(--fd-success-50)] flex items-center justify-center mx-auto mb-6">
            <CheckCircle2 className="w-12 h-12 text-[var(--fd-success-600)]" />
          </div>
          <h1 className="text-4xl mb-3" style={{ fontWeight: 700 }}>
            주문 완료!
          </h1>
          <p className="text-lg text-[var(--fd-fg-muted)]">
            구매해주셔서 감사합니다. 주문이 처리되고 있습니다.
          </p>
        </div>

        {/* Order Details Card */}
        <Card className="p-8 mb-6">
          <div className="flex items-start justify-between mb-6 pb-6 border-b border-[var(--fd-border-default)]">
            <div>
              <p className="text-sm text-[var(--fd-fg-muted)] mb-1">주문 번호</p>
              <p className="text-lg" style={{ fontWeight: 600 }}>
                {order.id}
              </p>
            </div>
            <div className="text-right">
              <p className="text-sm text-[var(--fd-fg-muted)] mb-1">결제 금액</p>
              <p className="text-2xl text-[var(--fd-primary-600)]" style={{ fontWeight: 700 }}>
                ${order.total.toFixed(2)}
              </p>
            </div>
          </div>

          {/* Items */}
          <div className="space-y-4">
            <h3 style={{ fontWeight: 600 }}>주문 상품</h3>
            {order.items.map((item) => (
              <div key={item.id} className="flex gap-4">
                <div className="w-20 h-20 rounded-lg overflow-hidden bg-[var(--fd-gray-100)] flex-shrink-0">
                  <ImageWithFallback
                    src={item.image}
                    alt={item.title}
                    className="w-full h-full object-cover"
                  />
                </div>
                <div className="flex-1">
                  <p style={{ fontWeight: 500 }}>{item.title}</p>
                  <p className="text-sm text-[var(--fd-fg-muted)]">
                    수량: {item.quantity} × ${item.price.sale.toFixed(2)}
                  </p>
                </div>
                <div className="text-right">
                  <p style={{ fontWeight: 600 }}>
                    ${(item.price.sale * item.quantity).toFixed(2)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Status Info */}
        <Card className="p-6 mb-8">
          <div className="flex items-start gap-4">
            <div className="w-12 h-12 rounded-lg bg-[var(--fd-info-50)] flex items-center justify-center flex-shrink-0">
              <Package className="w-6 h-6 text-[var(--fd-info-600)]" />
            </div>
            <div className="flex-1">
              <h3 className="mb-1" style={{ fontWeight: 600 }}>
                다음 단계는?
              </h3>
              <p className="text-sm text-[var(--fd-fg-muted)]">
                주문을 배송 준비 중입니다. 상품이 배송되면 추적 정보가 포함된 이메일을 받으실 수 있습니다. 
                대부분의 주문은 2-3 영업일 이내에 배송됩니다.
              </p>
            </div>
          </div>
        </Card>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-4">
          <Button
            className="flex-1"
            onClick={() => navigate('/orders')}
            style={{ backgroundColor: 'var(--fd-primary-600)' }}
          >
            모든 주문 보기
            <ArrowRight className="w-4 h-4 ml-2" />
          </Button>
          <Button
            variant="outline"
            className="flex-1"
            onClick={() => navigate('/deals')}
          >
            쇼핑 계속하기
          </Button>
        </div>
      </div>
    </div>
  );
}
