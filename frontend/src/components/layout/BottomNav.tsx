import { NavLink } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Users, Scale, Shuffle, History } from 'lucide-react'
import { cn } from '@/lib/utils'

const NAV_ITEMS = [
  { path: '/students', icon: Users, label: 'nav.students' },
  { path: '/compatibility', icon: Scale, label: 'nav.compatibility' },
  { path: '/generate', icon: Shuffle, label: 'nav.generateGroups' },
  { path: '/history', icon: History, label: 'nav.history' },
] as const

export default function BottomNav() {
  const { t } = useTranslation()

  return (
    <nav className="md:hidden shrink-0 border-t border-border bg-sidebar">
      <div className="flex">
        {NAV_ITEMS.map(({ path, icon: Icon, label }) => (
          <NavLink
            key={path}
            to={path}
            className={({ isActive }) =>
              cn(
                'flex-1 flex flex-col items-center gap-1 py-2 text-[11px] font-medium transition-colors',
                isActive
                  ? 'text-sidebar-primary'
                  : 'text-muted-foreground hover:text-sidebar-foreground'
              )
            }
          >
            <Icon className="h-5 w-5" />
            <span>{t(label)}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  )
}
