import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { GraduationCap } from 'lucide-react'
import Sidebar from './Sidebar'
import BottomNav from './BottomNav'
import ThemeToggle from './ThemeToggle'
import { useTheme } from '@/hooks/useTheme'

export default function AppLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const { theme, toggle } = useTheme()
  const { t } = useTranslation()

  return (
    <div className="flex h-screen bg-background">
      <Sidebar
        collapsed={collapsed}
        onToggleCollapse={() => setCollapsed(c => !c)}
        theme={theme}
        onToggleTheme={toggle}
      />

      <div className="flex-1 flex flex-col min-h-0">
        <header className="md:hidden flex items-center justify-between h-14 px-4 border-b border-border bg-sidebar shrink-0">
          <div className="flex items-center gap-2">
            <GraduationCap className="h-5 w-5 text-primary" />
            <span className="font-semibold text-sm text-sidebar-foreground">
              {t('app.title')}
            </span>
          </div>
          <ThemeToggle theme={theme} onToggle={toggle} />
        </header>

        <main className="flex-1 overflow-y-auto">
          <Outlet />
        </main>

        <BottomNav />
      </div>
    </div>
  )
}
