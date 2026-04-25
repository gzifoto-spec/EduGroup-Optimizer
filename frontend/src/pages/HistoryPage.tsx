import { useTranslation } from 'react-i18next'
import { History } from 'lucide-react'

export default function HistoryPage() {
  const { t } = useTranslation()

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-foreground">{t('nav.history')}</h1>
        <p className="text-muted-foreground mt-1 text-sm">{t('pages.history.description')}</p>
      </div>
      <div className="flex items-center justify-center h-64 rounded-xl border-2 border-dashed border-border">
        <div className="text-center space-y-2">
          <History className="h-10 w-10 text-muted-foreground/40 mx-auto" />
          <p className="text-sm text-muted-foreground">{t('common.comingSoon')}</p>
        </div>
      </div>
    </div>
  )
}
