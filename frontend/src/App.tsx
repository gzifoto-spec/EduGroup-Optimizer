import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { TooltipProvider } from '@/components/ui/tooltip'
import AppLayout from '@/components/layout/AppLayout'
import StudentsPage from '@/pages/StudentsPage'
import CompatibilityPage from '@/pages/CompatibilityPage'
import GenerateGroupsPage from '@/pages/GenerateGroupsPage'
import HistoryPage from '@/pages/HistoryPage'

export default function App() {
  return (
    <BrowserRouter>
      <TooltipProvider>
        <Routes>
          <Route path="/" element={<AppLayout />}>
            <Route index element={<Navigate to="/students" replace />} />
            <Route path="students" element={<StudentsPage />} />
            <Route path="compatibility" element={<CompatibilityPage />} />
            <Route path="generate" element={<GenerateGroupsPage />} />
            <Route path="history" element={<HistoryPage />} />
          </Route>
        </Routes>
      </TooltipProvider>
    </BrowserRouter>
  )
}
