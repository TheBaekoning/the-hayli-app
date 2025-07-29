<svelte:options runes={true} />
<script lang="ts">
	import { goto } from "$app/navigation";
	import ButtonLink from "$components/button-link/button-link.svelte";
	import Input from "$components/inputs/input.svelte";
	import { session, login as sessionLogin, type LoginResponse } from "$components/session/session.store";
	import { EButtonType } from "$lib";
	import { fromStore } from "svelte/store";

	let error = $state(null);
	let email = $state('');
	let password = $state('');

	function login(e){
		e.preventDefault();
		sessionLogin({email, password})
			.catch((err: LoginResponse) => {
				console.error(err.error.message);
				error = err.error.message;
			});
	}
	const sess = fromStore(session);
	$effect(() => {
		const loggedIn = sess.current.loggedIn;
		if (typeof loggedIn === 'boolean' && loggedIn){
			goto('/health');
		}
	});
</script>

<svelte:head>
	<title>Login</title>
</svelte:head>
<main class="index">
	<header>
		<h1>Login to access your dashboard</h1>
	</header>

	<div aria-hidden="true"></div>
	
	<form action="" class="login-form" novalidate onsubmit={login}>
		<div aria-live="assertive">
			{#if error}
				<p class="error">{error}</p>
			{/if}
		</div>
		<Input type="email" required={true} bind:value={email} />
		<Input type="password" required={true} bind:value={password} />
		<div><a href="/login/account-recovery">Forgot password?</a></div>
		<div class="login">
			<ButtonLink type={EButtonType.submit} class="login">Login</ButtonLink>
		</div>
		<p class="sign-up-text">Don&rsquo;t have an account? <a href="/sign-up">Sign Up</a>.</p>
	</form>
</main>


<style lang="scss">
	main.index{
		display: flex;
		flex-flow: column;
		justify-content: space-between;
		padding: 20px var(--side-spacing) 60px;
		height: calc(100vh - 80px - 45px - 2rem);
	}
	.login{
		margin-top: 33%;
		width: 100%;
	}
	.sign-up-text{
		margin-top: -15px;
		font: 0.875rem var(--ff);
		color: var(--dark2);
		text-align: center;
		width: calc(100% - 2 * var(--side-spacing));
		a{
			padding-left: 3px;
			color: var(--gradient3);
			font-weight: 700;
			text-decoration: none;
		}
	}
	@media (prefers-color-scheme: dark){
		.sign-up-text{
			color: var(--gradient3);
		}
	}
</style>