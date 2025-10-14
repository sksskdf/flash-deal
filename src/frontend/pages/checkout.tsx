import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApp } from '../lib/app-context';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Card } from '../components/ui/card';
import { Separator } from '../components/ui/separator';
import { toast } from 'sonner@2.0.3';
import { X, ShoppingBag, CreditCard, MapPin } from 'lucide-react';
import { ImageWithFallback } from '../components/image-with-fallback';

export function CheckoutPage() {
  const navigate = useNavigate();
  const { cart, removeFromCart, clearCart, placeOrder, user } = useApp();
  const [isProcessing, setIsProcessing] = useState(false);

  if (!user) {
    navigate('/auth', { state: { returnTo: '/checkout' } });
    return null;
  }

  const subtotal = cart.reduce((sum, item) => sum + item.price.sale * item.quantity, 0);
  const shipping = subtotal > 100 ? 0 : 9.99;
  const total = subtotal + shipping;

  const handleCheckout = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    
    if (cart.length === 0) {
      toast.error('장바구니가 비어있습니다');
      return;
    }

    setIsProcessing(true);

    // Simulate payment processing
    setTimeout(() => {
      const order = placeOrder(cart);
      clearCart();
      setIsProcessing(false);
      navigate(`/order/${order.id}`);
      toast.success('주문이 완료되었습니다!');
    }, 2000);
  };

  if (cart.length === 0) {
    return (
      <div className="min-h-screen bg-[var(--fd-bg-app)] flex items-center justify-center">
        <div className="text-center">
          <div className="w-24 h-24 rounded-full bg-[var(--fd-gray-100)] flex items-center justify-center mx-auto mb-4">
            <ShoppingBag className="w-12 h-12 text-[var(--fd-fg-muted)]" />
          </div>
          <h2 className="text-2xl mb-2" style={{ fontWeight: 600 }}>
            장바구니가 비어있습니다
          </h2>
          <p className="text-[var(--fd-fg-muted)] mb-6">
            플래시 딜을 담아보세요!
          </p>
          <Button onClick={() => navigate('/deals')}>
            딜 둘러보기
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[var(--fd-bg-app)]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl mb-8" style={{ fontWeight: 700 }}>
          주문하기
        </h1>

        <form onSubmit={handleCheckout}>
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Left Column - Forms */}
            <div className="lg:col-span-2 space-y-6">
              {/* Shipping Address */}
              <Card className="p-6">
                <div className="flex items-center gap-2 mb-4">
                  <MapPin className="w-5 h-5 text-[var(--fd-primary-600)]" />
                  <h2 className="text-xl" style={{ fontWeight: 600 }}>
                    배송 주소
                  </h2>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="firstName">이름</Label>
                    <Input id="firstName" required className="mt-1" />
                  </div>
                  <div>
                    <Label htmlFor="lastName">성</Label>
                    <Input id="lastName" required className="mt-1" />
                  </div>
                  <div className="sm:col-span-2">
                    <Label htmlFor="address">주소</Label>
                    <Input id="address" required className="mt-1" />
                  </div>
                  <div>
                    <Label htmlFor="city">도시</Label>
                    <Input id="city" required className="mt-1" />
                  </div>
                  <div>
                    <Label htmlFor="zip">우편번호</Label>
                    <Input id="zip" required className="mt-1" />
                  </div>
                </div>
              </Card>

              {/* Payment */}
              <Card className="p-6">
                <div className="flex items-center gap-2 mb-4">
                  <CreditCard className="w-5 h-5 text-[var(--fd-primary-600)]" />
                  <h2 className="text-xl" style={{ fontWeight: 600 }}>
                    결제 방법
                  </h2>
                </div>
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="cardNumber">카드 번호</Label>
                    <Input 
                      id="cardNumber" 
                      placeholder="1234 5678 9012 3456"
                      required 
                      className="mt-1" 
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="expiry">유효기간</Label>
                      <Input 
                        id="expiry" 
                        placeholder="MM/YY"
                        required 
                        className="mt-1" 
                      />
                    </div>
                    <div>
                      <Label htmlFor="cvv">CVV</Label>
                      <Input 
                        id="cvv" 
                        placeholder="123"
                        type="password"
                        required 
                        className="mt-1" 
                      />
                    </div>
                  </div>
                </div>
              </Card>
            </div>

            {/* Right Column - Order Summary */}
            <div className="lg:col-span-1">
              <Card className="p-6 sticky top-24">
                <h2 className="text-xl mb-4" style={{ fontWeight: 600 }}>
                  주문 요약
                </h2>

                {/* Cart Items */}
                <div className="space-y-4 mb-6">
                  {cart.map((item) => (
                    <div key={item.id} className="flex gap-3">
                      <div className="w-16 h-16 rounded-lg overflow-hidden bg-[var(--fd-gray-100)] flex-shrink-0">
                        <ImageWithFallback
                          src={item.image}
                          alt={item.title}
                          className="w-full h-full object-cover"
                        />
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm line-clamp-2" style={{ fontWeight: 500 }}>
                          {item.title}
                        </p>
                        <p className="text-sm text-[var(--fd-fg-muted)]">
                          수량: {item.quantity}
                        </p>
                        <p className="text-sm" style={{ fontWeight: 600 }}>
                          ${item.price.sale.toFixed(2)}
                        </p>
                      </div>
                      <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        className="flex-shrink-0"
                        onClick={() => removeFromCart(item.id)}
                      >
                        <X className="w-4 h-4" />
                      </Button>
                    </div>
                  ))}
                </div>

                <Separator className="my-4" />

                {/* Price Breakdown */}
                <div className="space-y-2 mb-6">
                  <div className="flex justify-between text-sm">
                    <span className="text-[var(--fd-fg-muted)]">소계</span>
                    <span>${subtotal.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-[var(--fd-fg-muted)]">배송비</span>
                    <span>
                      {shipping === 0 ? (
                        <span className="text-[var(--fd-success-600)]">무료</span>
                      ) : (
                        `$${shipping.toFixed(2)}`
                      )}
                    </span>
                  </div>
                  {subtotal < 100 && shipping > 0 && (
                    <p className="text-xs text-[var(--fd-info-600)]">
                      ${(100 - subtotal).toFixed(2)} 더 구매하시면 무료 배송
                    </p>
                  )}
                </div>

                <Separator className="my-4" />

                <div className="flex justify-between mb-6">
                  <span style={{ fontWeight: 600 }}>합계</span>
                  <span className="text-xl text-[var(--fd-primary-600)]" style={{ fontWeight: 700 }}>
                    ${total.toFixed(2)}
                  </span>
                </div>

                <Button 
                  type="submit"
                  className="w-full" 
                  disabled={isProcessing}
                  style={{ backgroundColor: 'var(--fd-primary-600)' }}
                >
                  {isProcessing ? '처리 중...' : '주문하기'}
                </Button>
              </Card>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
