<svelte:options runes={true} />
<script lang="ts">
	import Input from '$components/inputs/input.svelte';
	import ButtonLink from '$components/button-link/button-link.svelte';
	import { EButtonType } from '$lib';
	import { apiFetch } from '$lib/api-fetch';
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import { fromStore } from 'svelte/store';
	
	let error = $state('');
	let verificationCode = $state('');
	let sending = $state(false);
	let formEl: HTMLFormElement = $state(null);
	let p = fromStore(page);

	function verifyCode(e: Event){
		e.preventDefault();
		sending = true;
		const url = new URL('/login/verify', import.meta.env.VITE_API_URL);
		url.searchParams.set('encoding', verificationCode);
		apiFetch({
			url,
			options: {
				method: 'POST'
			}
		}).then(resp => {
			if(resp.ok){
				goto('/login');
			} else {
				resp.json().then(obj => {
					error = obj.error || obj.errors[0].message;
				});
			}
		}, err => {
			error = err.message;
		}).finally(() => {
			sending = false;
		});
	}
	$effect(() => {
		const code = p.current.url.searchParams.get('encoding');
		if (code){
			verificationCode = code;
			formEl.requestSubmit();
		}
	});
</script>

<main class="index">
	<header>
		<div class="breadcrumbs"><a href="/sign-up">Back</a></div>
		<h1>Create an account.</h1>
	</header>
	{#if error}
		<p class="error">{error}</p>
	{/if}
	<p class="success">Check your email for a verification code and enter the code below to verify your account.</p>
	<form bind:this={formEl} action="" method="post" onsubmit={verifyCode} class:sending>
		<Input type="text" required bind:value={verificationCode} />
		<div class="signup">
			<ButtonLink type={EButtonType.submit}>Verify</ButtonLink>
		</div>
	</form>
</main>
<style lang="scss">
	main{
		height: 100%;
	}
	form{
		height: 100%;
		.signup{
			margin-top: auto;
		}
		&.sending{
			opacity: 0.5;
			pointer-events: none;
		}
	}
</style>