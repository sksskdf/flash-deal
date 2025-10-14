interface PriceTagProps {
  original: number;
  sale: number;
  emphasize?: boolean;
}

export function PriceTag({ original, sale, emphasize }: PriceTagProps) {
  const discount = Math.round(((original - sale) / original) * 100);

  return (
    <div className="flex items-baseline gap-2 flex-wrap">
      <span
        className={emphasize ? 'text-3xl' : 'text-2xl'}
        style={{ fontWeight: 700, color: 'var(--fd-primary-600)' }}
      >
        ${sale.toFixed(2)}
      </span>
      <span
        className={emphasize ? 'text-lg' : 'text-base'}
        style={{ 
          textDecoration: 'line-through',
          color: 'var(--fd-fg-muted)'
        }}
      >
        ${original.toFixed(2)}
      </span>
      <span
        className={emphasize ? 'text-base' : 'text-sm'}
        style={{ 
          fontWeight: 600,
          color: 'var(--fd-danger-600)'
        }}
      >
        {discount}% OFF
      </span>
    </div>
  );
}

