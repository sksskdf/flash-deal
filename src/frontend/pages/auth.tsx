import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useApp } from '../lib/app-context';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Card } from '../components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { toast } from 'sonner@2.0.3';
import { Zap } from 'lucide-react';

export function AuthPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, signup } = useApp();
  const [isLoading, setIsLoading] = useState(false);

  const returnTo = (location.state as any)?.returnTo || '/';

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);

    const formData = new FormData(e.currentTarget);
    const email = formData.get('email') as string;
    const password = formData.get('password') as string;

    // Simulate API call
    setTimeout(() => {
      login(email, password);
      toast.success('환영합니다!');
      navigate(returnTo);
      setIsLoading(false);
    }, 1000);
  };

  const handleSignup = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);

    const formData = new FormData(e.currentTarget);
    const name = formData.get('name') as string;
    const email = formData.get('email') as string;
    const password = formData.get('password') as string;

    // Simulate API call
    setTimeout(() => {
      signup(name, email, password);
      toast.success('계정이 생성되었습니다!');
      navigate(returnTo);
      setIsLoading(false);
    }, 1000);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[var(--fd-primary-50)] via-white to-[var(--fd-info-50)] flex items-center justify-center p-4">
      <Card className="w-full max-w-md p-8" style={{ boxShadow: 'var(--fd-shadow-lg)' }}>
        {/* Logo */}
        <div className="flex items-center justify-center gap-2 mb-8">
          <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-[var(--fd-primary-600)] to-[var(--fd-info-600)] flex items-center justify-center">
            <Zap className="w-6 h-6 text-white" />
          </div>
          <span className="text-2xl" style={{ fontWeight: 700 }}>
            FlashDeal
          </span>
        </div>

        <Tabs defaultValue="login" className="w-full">
          <TabsList className="grid w-full grid-cols-2 mb-6">
            <TabsTrigger value="login">로그인</TabsTrigger>
            <TabsTrigger value="signup">회원가입</TabsTrigger>
          </TabsList>

          <TabsContent value="login">
            <form onSubmit={handleLogin} className="space-y-4">
              <div>
                <Label htmlFor="email">이메일</Label>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="your@email.com"
                  required
                  className="mt-1"
                />
              </div>
              <div>
                <Label htmlFor="password">비밀번호</Label>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="••••••••"
                  required
                  className="mt-1"
                />
              </div>
              <Button 
                type="submit" 
                className="w-full" 
                disabled={isLoading}
                style={{ backgroundColor: 'var(--fd-primary-600)' }}
              >
                {isLoading ? '로그인 중...' : '로그인'}
              </Button>
            </form>
          </TabsContent>

          <TabsContent value="signup">
            <form onSubmit={handleSignup} className="space-y-4">
              <div>
                <Label htmlFor="name">이름</Label>
                <Input
                  id="name"
                  name="name"
                  type="text"
                  placeholder="홍길동"
                  required
                  className="mt-1"
                />
              </div>
              <div>
                <Label htmlFor="signup-email">이메일</Label>
                <Input
                  id="signup-email"
                  name="email"
                  type="email"
                  placeholder="your@email.com"
                  required
                  className="mt-1"
                />
              </div>
              <div>
                <Label htmlFor="signup-password">비밀번호</Label>
                <Input
                  id="signup-password"
                  name="password"
                  type="password"
                  placeholder="••••••••"
                  required
                  className="mt-1"
                />
              </div>
              <Button 
                type="submit" 
                className="w-full" 
                disabled={isLoading}
                style={{ backgroundColor: 'var(--fd-primary-600)' }}
              >
                {isLoading ? '계정 생성 중...' : '계정 만들기'}
              </Button>
            </form>
          </TabsContent>
        </Tabs>

        <div className="mt-6 text-center">
          <Button 
            variant="ghost" 
            onClick={() => navigate(returnTo)}
            className="text-sm text-[var(--fd-fg-muted)]"
          >
            계정 없이 계속 둘러보기
          </Button>
        </div>
      </Card>
    </div>
  );
}
