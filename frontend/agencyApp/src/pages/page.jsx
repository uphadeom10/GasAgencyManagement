import React from 'react';
//import MainLayout from './MainLayout';
import MainLayout from '../layouts/MainLayout';

const Page = ({ children, title, username, role }) => {
  return (
    <MainLayout username={username} role={role}>
      <div className="">
        {/* Page Header */}
 
        {/* Page Content */}
        
          <div className="">
            {children}
          </div>
        
      </div>
    </MainLayout>
  );
};

export default Page;