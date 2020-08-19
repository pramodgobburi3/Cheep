"""cheep URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from users.views import *
from django.conf.urls import url, include
from plaid_api.views import *
from transactions.views import *
from accounts.views import *
from categories.views import *
from budgets.views import *
from jars.views import *

urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/register/', user_register),
    path('user/login/', get_user_info),
    path('api/social/', include('rest_framework_social_oauth2.urls')),
    path('plaid/access/', public_to_access),
    path('user/transactions/', fetch_transactions),
    path('auth/', get_auth_req),
    path('user/accounts/create/', create_account),
    path('categories/populate/', populate_categories),
    path('user/budgets/', get_budgets),
    path('user/budgets/add/', add_budget),
    path('user/jars/add/', add_jar),
    path('user/jar/update/', update_jars),
    path('user/finance/all/', fetch_all_financial_data)
]
