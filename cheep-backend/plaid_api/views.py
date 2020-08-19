from django.shortcuts import render
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from oauthlib.common import generate_token
from oauth2_provider.models import AccessToken, Application, RefreshToken
from django.utils import timezone
from users.models import UserProfile
from .models import PlaidProfile
from .api import *
from .serializers import PlaidProfileSerializer

# Create your views here.

@csrf_exempt
def public_to_access(request):

    if request.method == "POST":
        if request.POST.get("access_token") is None:
            return JsonResponse({"status": "No access token provided"}, safe=False)
        else:
            try:
                access_token = AccessToken.objects.get(token=request.POST.get("access_token"),
                                                       expires__gt=timezone.now())
            except:
                try:
                    access_token = AccessToken.objects.get(token=request.POST.get("access_token"))
                except:
                    return JsonResponse({"status": "Invalid token"})
                else:
                    return JsonResponse({"status": "Access token has expired. Request a new one."})
            else:
                public_token = request.POST.get("public_token")
                user = access_token.user
                user_profile = UserProfile.objects.get(user_id=user.id)
                access_token = get_access_token(public_token)
                plaid_user_profile = PlaidProfile.objects.create(user=user_profile, access_token=access_token)
                return JsonResponse({"status": "Successful", "plaid_profile": PlaidProfileSerializer(plaid_user_profile).data}, safe=False)


@csrf_exempt
def get_auth_req(request):
    if request.method == "POST":
        if request.POST.get("access_token") is None:
            return JsonResponse({"status": "No access token provided"}, safe=False)
        else:
            try:
                access_token = AccessToken.objects.get(token=request.POST.get("access_token"),
                                                       expires__gt=timezone.now())
            except:
                try:
                    access_token = AccessToken.objects.get(token=request.POST.get("access_token"))
                except:
                    return JsonResponse({"status": "Invalid token"})
                else:
                    return JsonResponse({"status": "Access token has expired. Request a new one."})
            else:
                plaid_access_token = request.POST.get("plaid_token")
                auth = get_auth(plaid_access_token)
                return JsonResponse({"status": "Successful", "auth": auth}, safe=False)
